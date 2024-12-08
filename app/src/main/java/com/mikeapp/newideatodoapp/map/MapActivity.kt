package com.mikeapp.newideatodoapp.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat.getCurrentLocation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mikeapp.newideatodoapp.BuildConfig
import com.mikeapp.newideatodoapp.BuildConfig.googleMapApiKey
import com.mikeapp.newideatodoapp.R
import com.mikeapp.newideatodoapp.geo.GeofenceUseCase.Companion.LOCATION_PERMISSION_REQUEST_CODE
import com.mikeapp.newideatodoapp.ui.extension.adaptEdgeToEdge
import kotlin.math.ln

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
            MapScreen(onLocationSelected = { latLng, radius ->
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

@Composable
fun MapScreen(onLocationSelected: (LatLng, Double) -> Unit) {
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }

    var addressText by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var diameterText by remember { mutableStateOf("") }
    var isDropdownVisible by remember { mutableStateOf(false) }
    var zoomLevel by remember { mutableDoubleStateOf(15.0) }
    val cameraPositionState = rememberCameraPositionState {
        zoomLevel = 15f - ln(diameterText.toDoubleOrNull() ?: 1.0) / ln(2.0)
        position = CameraPosition.fromLatLngZoom(
            LatLng(-33.8688, 151.2093),
            zoomLevel.toFloat()
        )
    }

    // Autocomplete function for address input
    val autocomplete = rememberUpdatedState({ query: String ->
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                suggestions =
                    response.autocompletePredictions.map { it.getFullText(null).toString() }
            }
    })

    // Update suggestions as user types
    LaunchedEffect(addressText) {
        autocomplete.value(addressText)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = addressText,
            onValueChange = { value ->
                addressText = value
                isDropdownVisible = value.isNotEmpty() // Show dropdown when there's text
            },
            label = { Text("Search Address") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            singleLine = true
        )
        // Display suggestions
        if (isDropdownVisible) {
            suggestions.forEach { suggestion ->
                Text(
                    text = suggestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            // Use Geocoder or Places API to get LatLng and update selectedLocation
                            val geocoder = Geocoder(context)
                            val results = geocoder.getFromLocationName(suggestion, 1)
                            results
                                ?.firstOrNull()
                                ?.let {
                                    selectedLocation = LatLng(it.latitude, it.longitude)
                                    cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(
                                        selectedLocation!!, zoomLevel.toFloat()))
                                }
                            isDropdownVisible = false // Dismiss the dropdown
                        }
                        .padding(8.dp)
                )
            }
        }

        // Map UI
        Box(modifier = Modifier.weight(1f)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng -> selectedLocation = latLng }
            ) {
                selectedLocation?.let {
                    Marker(state = MarkerState(position = it))
                    val radius = (diameterText.toDoubleOrNull() ?: 0.0) / 2
                    Circle(
                        center = it,
                        radius = radius,
                        fillColor = Color(0x550000FF),
                        strokeColor = Color.Blue,
                        strokeWidth = 2f
                    )
                }
            }
            FloatingActionButton(
                onClick = {
                    getCurrentLocation(context) { latLng ->
                        // Update the map's camera position
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel.toFloat()))
                    }
                },
                modifier = Modifier
                    .padding(16.dp)
                    .padding(bottom = 100.dp)
                    .align(Alignment.BottomEnd)
            ) {
                Icon(painter = painterResource(id = R.drawable.baseline_my_location_24), contentDescription = "Current Location")
            }
        }

        // Radius and confirmation section
        Surface(modifier = Modifier.fillMaxWidth(), tonalElevation = 4.dp) {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = diameterText,
                    onValueChange = { diameterText = it },
                    label = { Text("Diameter (meters)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val radius = diameterText.toDoubleOrNull()?.div(2) ?: 0.0
                        selectedLocation?.let {
                            onLocationSelected(it, radius)
                        }
                    },
                    enabled = selectedLocation != null && diameterText.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Confirm Location")
                }
            }
        }
    }
}

private fun getCurrentLocation(context: Context, action: (LatLng) -> Unit) {
    val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    val locationResult: Task<Location> = if (ActivityCompat.checkSelfPermission(
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

        return
    } else fusedLocationClient.lastLocation
    locationResult.addOnSuccessListener { location ->
        if (location != null) {
            val latLng = LatLng(location.latitude, location.longitude)
            action.invoke(latLng)
        }
    }
}