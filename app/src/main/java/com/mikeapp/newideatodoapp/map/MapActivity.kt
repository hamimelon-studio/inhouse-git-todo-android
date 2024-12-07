package com.mikeapp.newideatodoapp.map

import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mikeapp.newideatodoapp.BuildConfig.googleMapApiKey
import kotlin.math.ln

class MapActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Places API with your API key
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, googleMapApiKey)
        }
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
                cameraPositionState = rememberCameraPositionState {
                    val zoomLevel = 15f - ln(diameterText.toDoubleOrNull() ?: 1.0) / ln(2.0)
                    position = CameraPosition.fromLatLngZoom(LatLng(-33.8688, 151.2093), zoomLevel.toFloat())
                },
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