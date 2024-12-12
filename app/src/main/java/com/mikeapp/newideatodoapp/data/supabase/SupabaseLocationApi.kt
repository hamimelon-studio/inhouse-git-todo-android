package com.mikeapp.newideatodoapp.data.supabase

import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseLocation
import retrofit2.http.GET
import retrofit2.http.Query

interface SupabaseLocationApi {
    @GET("/rest/v1/location")
    suspend fun getLocation(
        @Query("user") user: String
    ): List<SupabaseLocation>
}