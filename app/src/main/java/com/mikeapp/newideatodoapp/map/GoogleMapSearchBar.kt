package com.mikeapp.newideatodoapp.map

import android.location.Geocoder
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.mikeapp.newideatodoapp.BuildConfig
import com.mikeapp.newideatodoapp.R
import com.mikeapp.newideatodoapp.ui.theme.UltraLightGray

@Composable
fun GoogleMapSearchBar(modifier: Modifier, onSelect: (String, LatLng) -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if (!Places.isInitialized()) {
            Places.initialize(context, BuildConfig.MAPS_API_KEY)
        }
    }

    val placesClient = remember { Places.createClient(context) }
    var searchQuery by remember { mutableStateOf("") }
    var suggestions by remember { mutableStateOf<List<String>>(emptyList()) }
    var isDropdownVisible by remember { mutableStateOf(false) }
    var showFullScreen by remember { mutableStateOf(false) }

    // Autocomplete function for search query
    val autocomplete = rememberUpdatedState { query: String ->
        val request = FindAutocompletePredictionsRequest.builder()
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response ->
                suggestions =
                    response.autocompletePredictions.map { it.getFullText(null).toString() }
            }
    }

    LaunchedEffect(searchQuery) {
        autocomplete.value(searchQuery)
        isDropdownVisible = searchQuery.isNotEmpty() // Show dropdown when there's text
    }

    if (showFullScreen) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {

        }
    }

    Column {
        TextField(
            value = searchQuery,
            onValueChange = { value ->
                searchQuery = value
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp)
                .onFocusChanged { focusState ->
                    showFullScreen = focusState.isFocused
                },
            placeholder = {
                Text("Search...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear",
                        modifier = Modifier.clickable {
                            searchQuery = ""
                        },
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = UltraLightGray,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                cursorColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                focusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
                unfocusedPlaceholderColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
            shape = RoundedCornerShape(percent = 50)
        )

        // Display suggestions
        if (isDropdownVisible) {
            suggestions.forEach { suggestion ->
                Row(
                    modifier = Modifier.heightIn(min = 48.dp),
                    verticalAlignment = CenterVertically
                ) {
                    Icon(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_location_pin_24),
                        contentDescription = "Location suggestion",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
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
                                        onSelect.invoke(suggestion, LatLng(it.latitude, it.longitude))
                                    }
                                isDropdownVisible = false
                                showFullScreen = false
                            }
                            .padding(8.dp)
                    )
                }
                HorizontalDivider(
                    thickness = 0.3.dp,
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
            }
        }
    }
}