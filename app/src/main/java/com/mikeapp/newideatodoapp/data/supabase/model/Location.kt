package com.mikeapp.newideatodoapp.data.supabase.model

data class Location(
    val id: Int,
    val name: String,
    val lat: Double,
    val lon: Double,
    val radius: Double,
    val user: Int
)