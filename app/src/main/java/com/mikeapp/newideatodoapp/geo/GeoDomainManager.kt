package com.mikeapp.newideatodoapp.geo

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.mikeapp.newideatodoapp.Constant.logTag
import com.mikeapp.newideatodoapp.data.exception.CodeLogicException
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity
import com.mikeapp.newideatodoapp.main.add.model.LocationUi
import java.time.LocalDate
import java.time.LocalTime

class GeoDomainManager(private val context: Context) {
    private val taskById = hashMapOf<Int, GeoTask>()
    private val geoTasks = hashMapOf<LocationUi, MutableList<GeoTask>>()
    private var geofencingClient: GeofencingClient = LocationServices.getGeofencingClient(context)
    private val pendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(
            context, GEO_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    fun addGeoTask(task: TaskEntity, locationUi: LocationUi) {
        val locationId = locationUi.id ?: throw CodeLogicException("locationUi.id == null")
        if (taskById.containsKey(task.id)) {
            return
        }

        val geoTask = GeoTask(
            locationId = locationId,
            locationUi = locationUi,
            geoId = locationUi.toString(),
            taskId = task.id,
            listId = task.list,
            date = task.due?.let { LocalDate.parse(it) },
            time = task.time?.let { LocalTime.parse(it) },
            alarmedCount = 0,
            dismissedByUser = false
        )
        taskById[task.id] = geoTask
        if (!geoTasks.containsKey(locationUi)) {
//            addGeoFence(locationUi)
            GeofenceUseCase(context).register()
            geoTasks[locationUi]?.add(geoTask)
        } else {
            geoTasks[locationUi] = mutableListOf(geoTask)
        }
    }

    fun removeGeoTask(task: TaskEntity, locationUi: LocationUi) {
        if (taskById.containsKey(task.id)) {
            taskById.remove(task.id)
        }
        if (geoTasks.containsKey(locationUi)) {
            val taskList = geoTasks[locationUi]
            taskList?.removeIf { it.taskId == task.id }
            if (taskList?.isEmpty() == true) {
                geoTasks.remove(locationUi)
                deleteGeoFence(locationUi.toString())
            }
        }
    }

    private fun deleteGeoFence(geoFenceId: String) {
        Log.d("bbbb", "deleteGeoFence: $geoFenceId")
        val geofenceIdsToRemove = listOf(geoFenceId)
        geofencingClient.removeGeofences(geofenceIdsToRemove).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(logTag, "Geofence for location: $geoFenceId delete succeed.")
            } else {
                Log.w(logTag, "Geofence for location: $geoFenceId delete failure. reason: $task")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun addGeoFence(locationUi: LocationUi) {
        Log.d("bbbb", "addGeoFence: $locationUi")
        val geoFence = buildGeofence(locationUi.toString(), locationUi.lat, locationUi.lat, locationUi.radius.toFloat())
        val geoFenceRequest = createRequest(geoFence)
        geofencingClient.addGeofences(geoFenceRequest, pendingIntent)
            .addOnSuccessListener {
                Log.d(logTag, "Geofence for location: $locationUi added succeed.")
            }
            .addOnFailureListener {
                Log.w(logTag, "Geofence for location: $locationUi added failure. ${it.localizedMessage}")
            }
    }

    private fun createRequest(geoFence: Geofence): GeofencingRequest {
        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofence(geoFence)
            .build()
        return geofencingRequest
    }

    private fun buildGeofence(name: String, lat: Double, lon: Double, radius: Float): Geofence {
        val geofence = Geofence.Builder()
            .setRequestId("Home location")
            .setCircularRegion(lat, lon, radius)
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

    companion object {
        private const val GEO_REQUEST_CODE = 0
    }
}