package com.mikeapp.newideatodoapp.map

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.mikeapp.newideatodoapp.R
import com.mikeapp.newideatodoapp.geo.GeofenceUseCase.Companion.LOCATION_PERMISSION_REQUEST_CODE
import kotlin.math.ln

@Composable
fun GoogleMapScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    var diameter by remember { mutableDoubleStateOf(200.0) }
    var zoomLevel by remember { mutableDoubleStateOf(16.0) }
    val cameraPositionState = rememberCameraPositionState {
        zoomLevel = 16.5f - ln(diameter) / ln(2.0)
        position = CameraPosition.fromLatLngZoom(
            LatLng(-33.8688, 151.2093),
            zoomLevel.toFloat()
        )
    }
    var showDiameterSelectDialog by remember { mutableStateOf(false) }
    var showInputNameDialog by remember { mutableStateOf(false) }
    LaunchedEffect(cameraPositionState.position) {
        zoomLevel = cameraPositionState.position.zoom.toDouble()
    }
    var nameFromSearch by remember { mutableStateOf<String?>(null) }

    // Map UI
    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            onMapClick = { latLng ->
                nameFromSearch = null
                selectedLocation = latLng
            }
        ) {
            selectedLocation?.let {
                Marker(state = MarkerState(position = it))
                val radius = (diameter) / 2
                Circle(
                    center = it,
                    radius = radius,
                    fillColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                    strokeColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f),
                    strokeWidth = 2f
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .padding(bottom = 100.dp)
                .align(Alignment.BottomEnd),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    showDiameterSelectDialog = true
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_radar_24),
                    contentDescription = "Select Diameter"
                )
            }
            FloatingActionButton(
                onClick = {
                    getCurrentLocation(context) { latLng ->
                        selectedLocation = latLng
                        zoomLevel = 17.0
                        cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(latLng, 17.0f))
                    }
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_my_location_24),
                    contentDescription = "Current Location"
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        GoogleMapSearchBar(modifier = Modifier.padding(top = 48.dp)) { locationName, latLng ->
            nameFromSearch = locationName
            LatLng(latLng.latitude, latLng.longitude).let { location ->
                selectedLocation = location
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(location, zoomLevel.toFloat()))
            }
        }

        // Radius and confirmation section
        Button(
            onClick = {
                selectedLocation?.let {
                    showInputNameDialog = true
                }
            },
            enabled = selectedLocation != null && diameter > 0,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(start = 32.dp, end = 64.dp, bottom = 32.dp)
                .padding(16.dp)
        ) {
            Text("Confirm Location")
        }
    }

    if (showDiameterSelectDialog) {
        DiameterPickerDialog(onDismiss = {
            showDiameterSelectDialog = false
        }) { d ->
            diameter = d
            showDiameterSelectDialog = false
            selectedLocation?.let {
                cameraPositionState.move(CameraUpdateFactory.newLatLngZoom(it, zoomLevel.toFloat()))
            }
        }
    }

    if (showInputNameDialog) {
        LocationInputDialog(
            nameFromSearch = nameFromSearch,
            latLng = selectedLocation,
            onDismiss = {
                showInputNameDialog = false
            },
            onLocationSelected = { locationName ->
                val radius = diameter / 2
                selectedLocation?.let { onLocationSelected(navController, locationName, it, radius) }
            }
        )
    }
}

private fun onLocationSelected(navController: NavController, locationName: String, latLng: LatLng, radius: Double) {
    navController.previousBackStackEntry?.savedStateHandle?.run {
        set("location_name", locationName)
        set("latLng", latLng)
        set("radius", radius)
    }
    navController.popBackStack()
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
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)

        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
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