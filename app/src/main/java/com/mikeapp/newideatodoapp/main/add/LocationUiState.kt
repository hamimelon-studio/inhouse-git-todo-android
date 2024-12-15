package com.mikeapp.newideatodoapp.main.add

data class LocationUiState(
    val locations: List<LocationUi> = emptyList()
)

data class LocationUi(
    val name: String,
    val lat: Double,
    val lon: Double,
    val radius: Double
)