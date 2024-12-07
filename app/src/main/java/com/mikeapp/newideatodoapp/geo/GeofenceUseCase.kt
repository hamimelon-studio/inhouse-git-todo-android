package com.mikeapp.newideatodoapp.geo

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.mikeapp.newideatodoapp.BuildConfig.testLocationHomeLat
import com.mikeapp.newideatodoapp.BuildConfig.testLocationHomeLon
import com.mikeapp.newideatodoapp.BuildConfig.testLocationWorkLat
import com.mikeapp.newideatodoapp.BuildConfig.testLocationWorkLon
import com.mikeapp.newideatodoapp.alarm.NotificationLauncher

class GeofenceUseCase(private val context: Context) {
    fun register() {
        val geofencingClient = LocationServices.getGeofencingClient(context)

//        val pendingIntent = PendingIntent.getBroadcast(
//            context,
//            0,
//            Intent(context, GeofenceBroadcastReceiver::class.java),
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
        val pendingIntent: PendingIntent by lazy {
            val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
            PendingIntent.getBroadcast(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE)
        }

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("bbbb", "permission check failed!")
            return
        }
        geofencingClient.addGeofences(createRequest(), pendingIntent)
            .addOnSuccessListener {
                // Geofence added successfully
                Log.d("bbbb", "Geofence added successfully")
                NotificationLauncher.notify(
                    context,
                    "Info",
                    "Geofence added successfully.",
                    "Geofence added successfully."
                )
            }
            .addOnFailureListener {
                // Handle failure
                Log.d("bbbb", "Geofence added failure")
                NotificationLauncher.notify(context, "Error", "Geofence added failure!!!")
            }
    }

    private fun createRequest(): GeofencingRequest {
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(buildGeofence(geofenceIdHome, testLocationHomeLat.toDouble(), testLocationHomeLon.toDouble()))
            .addGeofence(buildGeofence(geofenceIdWork, testLocationWorkLat.toDouble(), testLocationWorkLon.toDouble()))
            .build()
        return geofencingRequest
    }

    private fun buildGeofence(name: String, lat: Double, lon: Double): Geofence {
        val geofence = Geofence.Builder()
            .setRequestId(name)
            .setCircularRegion(lat, lon, 200f) // lat, long, radius (meters)
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER or
                        Geofence.GEOFENCE_TRANSITION_EXIT or
                        Geofence.GEOFENCE_TRANSITION_DWELL
            )
            .setLoiteringDelay(5000)
            .build()
        return geofence
    }

    fun requestPermissions(activity: Activity) {
        if (!checkPermissions()) {
            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )
            } else {
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
            }

            ActivityCompat.requestPermissions(
                activity,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun checkPermissions(): Boolean {
        val foregroundPermission = ActivityCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val backgroundPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_BACKGROUND_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Background permission not required for Android 9 and below
        }

        return foregroundPermission && backgroundPermission
    }

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 449

        const val geofenceIdHome = "geofence-home"

        const val geofenceIdWork = "geofence-work"
    }
}