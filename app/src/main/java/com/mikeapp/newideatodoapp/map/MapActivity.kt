package com.mikeapp.newideatodoapp.map

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.libraries.places.api.Places
import com.mikeapp.newideatodoapp.BuildConfig
import com.mikeapp.newideatodoapp.geo.GeofenceUseCase.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.mikeapp.newideatodoapp.ui.extension.adaptEdgeToEdge

class MapActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Places API with your API key
        Log.d("bbbb", "MAPS_API_KEY: ${BuildConfig.MAPS_API_KEY}")
//        initialisePlaceApi()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.MAPS_API_KEY)
        }
        enableEdgeToEdge()
        adaptEdgeToEdge(window.decorView)
        setContent {
            GoogleMapScreen(onLocationSelected = { latLng, radius ->
                val resultIntent = Intent().apply {
                    putExtra("latitude", latLng.latitude)
                    putExtra("longitude", latLng.longitude)
                    putExtra("radius", radius)
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            })
        }
    }

    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)} passing\n      in a {@link RequestMultiplePermissions} object for the {@link ActivityResultContract} and\n      handling the result in the {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Permission granted, proceed with your logic
            } else {
                // Permission denied, handle appropriately
            }
        }
    }

    private fun initialisePlaceApi() {
        // Define a variable to hold the Places API key.
        val apiKey = BuildConfig.MAPS_API_KEY

        // Log an error if apiKey is not set.
        if (apiKey.isEmpty() || apiKey == "DEFAULT_API_KEY") {
            Log.e("Places test", "No api key")
            finish()
            return
        }

        // Initialize the SDK
        Places.initializeWithNewPlacesApiEnabled(applicationContext, apiKey)

        // Create a new PlacesClient instance
        val placesClient = Places.createClient(this)

        Log.d("bbbb", "placesClient: $placesClient")
    }
}