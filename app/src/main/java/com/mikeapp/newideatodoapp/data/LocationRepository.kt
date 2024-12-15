package com.mikeapp.newideatodoapp.data

import com.mikeapp.newideatodoapp.data.room.TnnDatabase
import com.mikeapp.newideatodoapp.data.room.model.LocationEntity
import com.mikeapp.newideatodoapp.data.supabase.SupabaseNetworkModule

class LocationRepository(
    private val room: TnnDatabase,
    networkModule: SupabaseNetworkModule
) {
    private val locationApi = networkModule.supabaseLocationApi
    private val taskApi = networkModule.supabaseTaskApi

    suspend fun getLocationList(): List<LocationEntity> {
        return room.locationDao().getLocations()
    }

    private fun eq(value: String): String = "eq.$value"

    private fun eq(value: Int): String = "eq.$value"
}