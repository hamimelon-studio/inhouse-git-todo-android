package com.mikeapp.newideatodoapp.map

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.LatLng

@Composable
fun LocationInputDialog(
    nameFromSearch: String?,
    latLng: LatLng?,
    onDismiss: () -> Unit,
    onLocationSelected: (String) -> Unit
) {
    var locationName by remember { mutableStateOf("") }
    val defaultName = nameFromSearch ?: latLng.toString()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Enter Location")
        },
        text = {
            TextField(
                value = locationName,
                onValueChange = { locationName = it },
                placeholder = {
                    Text(defaultName)
                },
                modifier = Modifier
                    .fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    val finalName = if (locationName.trim().isEmpty()) {
                        defaultName
                    } else {
                        locationName
                    }
                    onDismiss()
                    onLocationSelected(finalName)
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}