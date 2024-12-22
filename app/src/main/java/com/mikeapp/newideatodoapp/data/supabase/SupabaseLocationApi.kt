package com.mikeapp.newideatodoapp.data.supabase

import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseLocation
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseLocationApi {
    @GET("/rest/v1/location")
    suspend fun getLocations(
        @Query("user") user: String
    ): List<SupabaseLocation>

    @GET("/rest/v1/location")
    suspend fun getLocation(
        @Query("id") id: String
    ): List<SupabaseLocation>

    @GET("/rest/v1/location")
    suspend fun getLastLocation(
        @Query("user") user: String,
        @Query("order") order: String = "id.desc",
        @Query("limit") limit: Int = 1
    ): List<SupabaseLocation>

    @POST("/rest/v1/location")
    suspend fun insertLocation(
        @Body supabaseLocation: SupabaseLocation
    ): Response<Unit>
}