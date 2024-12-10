package com.mikeapp.newideatodoapp.data.supabase

import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseVersion
import retrofit2.http.GET
import retrofit2.http.Query

interface SupabaseVersionApi {
    @GET("/rest/v1/version")
    suspend fun getVersion(
        @Query("user") user: String
    ): List<SupabaseVersion>
}