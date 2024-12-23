package com.mikeapp.newideatodoapp.geo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.mikeapp.newideatodoapp.alarm.NotificationLauncher
import com.mikeapp.newideatodoapp.geo.GeofenceUseCase.Companion.geofenceIdHome
import com.mikeapp.newideatodoapp.geo.GeofenceUseCase.Companion.geofenceIdWork

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent == null) {
            Log.d(
                "bbbb",
                "GeofenceBroadcastReceiver geofencingEvent: $geofencingEvent, intent: $intent"
            )
            NotificationLauncher.notify(
                context,
                "Error",
                "GeofenceBroadcastReceiver: geofencingEvent: $geofencingEvent, intent: $intent",
                "GeofenceBroadcastReceiver: geofencingEvent: " +
                        "$geofencingEvent, intent: $intent," +
                        "intent.action: ${intent.action}"
            )
//            saveLongMessage(context, "GeofenceBroadcastReceiver geofencingEvent: $geofencingEvent, intent: $intent")
            return
        }

        val geofenceIds = geofencingEvent.triggeringGeofences?.map { it.requestId }
        Log.d("bbbb", "geofenceIds: $geofenceIds --")
        if (geofenceIds == null) {
            Log.d("bbbb", "geofenceIds: $geofenceIds")
            NotificationLauncher.notify(
                context,
                "Error",
                "GeofenceBroadcastReceiver: geofenceIds: $geofenceIds"
            )
            saveLongMessage(context, "GeofenceBroadcastReceiver: geofenceIds: $geofenceIds")
            return
        }
        val location = if (geofenceIdHome in geofenceIds) {
            "Home location"
        } else if (geofenceIdWork in geofenceIds) {
            "Work location"
        } else {
            "Unknown location"
        }

        val event = if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            // Handle entering the geofence
            Log.d("bbbb", "entering the geofence")
            "entering"
        } else if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Handle exiting the geofence
            Log.d("bbbb", "exiting the geofence")
            "exiting"
        } else if (geofencingEvent.geofenceTransition == Geofence.GEOFENCE_TRANSITION_DWELL) {
            // Handle exiting the geofence
            Log.d("bbbb", "dwell in geofence")
            "dwelling for 5 seconds"
        } else {
            Log.d("bbbb", "geofence triggered but no idea why: geofencingEvent: $geofencingEvent")
            saveLongMessage(context, "geofence triggered but no idea why: geofencingEvent: $geofencingEvent")
            "unknown event $geofencingEvent"
        }

        val title = "Geofence Transition!"
        val message = "$event location: $location detected!"
        NotificationLauncher.notify(context, title, message)
    }

    private fun saveLongMessage(context: Context, longMessageToPass: String) {
        val sharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("extra_long_string", longMessageToPass)
        editor.apply() // Apply the changes asynchronously
    }
}
