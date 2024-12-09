package com.mikeapp.newideatodoapp.data.supabase.model

data class SupabaseLocation(
    val id: Int? = null,
    val name: String,
    val lat: Double,
    val lon: Double,
    val radius: Double,
    val user: Int
)