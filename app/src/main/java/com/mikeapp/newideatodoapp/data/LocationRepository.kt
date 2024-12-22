package com.mikeapp.newideatodoapp.data

import com.mikeapp.newideatodoapp.data.exception.BackendAppException
import com.mikeapp.newideatodoapp.data.exception.CodeLogicException
import com.mikeapp.newideatodoapp.data.room.TnnDatabase
import com.mikeapp.newideatodoapp.data.room.model.LocationEntity
import com.mikeapp.newideatodoapp.data.supabase.SupabaseNetworkModule
import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseLocation
import com.mikeapp.newideatodoapp.main.add.model.LocationUi

class LocationRepository(
    private val room: TnnDatabase,
    networkModule: SupabaseNetworkModule
) {
    private val userApi = networkModule.supabaseUserApi
    private val locationApi = networkModule.supabaseLocationApi

    suspend fun getLocationList(): List<LocationEntity> {
        return room.locationDao().getLocations()
    }

    suspend fun loadLocationFromCloud() {
        val user = room.userDao().getUser().firstOrNull()
            ?: throw CodeLogicException("user is null in loadLocationFromCloud().")
        val remoteLocationVersion = userApi.getUserById(eq(user.id)).firstOrNull()?.locationVersion ?: 0
        val localLocationVersion = user.locationVersion
        if (localLocationVersion < remoteLocationVersion) {
            val locationResponse = locationApi.getLocations(eq(user.id))
            val locationEntities = locationResponse.map {
                LocationEntity(
                    id = it.id ?: throw BackendAppException("location id from backend is null"),
                    name = it.name,
                    lat = it.lat,
                    lon = it.lon,
                    radius = it.radius
                )
            }
            room.userDao().updateLocationVersion(user.id, remoteLocationVersion)
            room.locationDao().clear()
            room.locationDao().saveAll(locationEntities)
        }
    }

    suspend fun deleteLocation(locationId: Int) {
        locationApi.deleteLocation(eq(locationId))
        room.locationDao().remove(locationId)
        val user =
            room.userDao().getUser().firstOrNull() ?: throw CodeLogicException("user is null in deleteLocation().")
        updateLocationVersion(user.id, System.currentTimeMillis())
    }

    suspend fun addLocation(locationUi: LocationUi) {
        val user = room.userDao().getUser().firstOrNull() ?: throw CodeLogicException("user is null in addLocation().")
        val locationRequest = SupabaseLocation(
            name = locationUi.name,
            lat = locationUi.lat,
            lon = locationUi.lon,
            radius = locationUi.radius,
            user = user.id
        )
        val response = locationApi.insertLocation(locationRequest)
        if (!response.isSuccessful) {
            throw BackendAppException("insert new location to supabase failed")
        }
        val locationResponse = locationApi.getLastLocation(eq(user.id)).firstOrNull()
            ?: throw BackendAppException("fetch new location from supabase failed")

        val locationId = locationResponse.id ?: throw BackendAppException("location id is null")
        val locationEntity = LocationEntity(
            id = locationId,
            name = locationResponse.name,
            lat = locationResponse.lat,
            lon = locationResponse.lon,
            radius = locationResponse.radius
        )
        room.locationDao().save(locationEntity)
        updateLocationVersion(user.id, System.currentTimeMillis())
    }

    suspend fun getLocationById(locationId: Int): LocationEntity {
        return room.locationDao().getLocation(locationId)
            ?: throw CodeLogicException("location is null in getLocationById()")
    }

    suspend fun getLastLocationFromDb(): LocationEntity {
        return room.locationDao().getLastLocation()
            ?: throw CodeLogicException("last location should not be null after just added a new one.")
    }

    private suspend fun updateLocationVersion(userId: Int, locationVersion: Long) {
        val partialUpdate = mapOf("locationVersion" to locationVersion)
        userApi.updateLong(eq(userId), partialUpdate)
        room.userDao().updateLocationVersion(userId, locationVersion)
    }

    private fun eq(value: String): String = "eq.$value"

    private fun eq(value: Int): String = "eq.$value"
}