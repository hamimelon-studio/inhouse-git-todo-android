package com.mikeapp.newideatodoapp.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.mikeapp.newideatodoapp.geo.GeofenceUseCase.Companion.LOCATION_PERMISSION_REQUEST_CODE

class MapPermissionManager {
     fun CheckLocationPermission(context: Context): Boolean {
         if (ActivityCompat.checkSelfPermission(
                 context,
                 Manifest.permission.ACCESS_FINE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                 context,
                 Manifest.permission.ACCESS_COARSE_LOCATION
             ) != PackageManager.PERMISSION_GRANTED
         ) {
             Log.d("bbbb", "no permission granted")

             val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

             if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 ActivityCompat.requestPermissions(context as Activity, permissions, LOCATION_PERMISSION_REQUEST_CODE)
             }
         }
         return true
    }
}