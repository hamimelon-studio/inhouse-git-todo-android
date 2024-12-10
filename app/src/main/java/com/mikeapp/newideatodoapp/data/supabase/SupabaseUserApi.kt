package com.mikeapp.newideatodoapp.data.supabase

import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseUserApi {
    @POST("/rest/v1/user")
    suspend fun createUser(
        @Body supabaseUser: SupabaseUser
    ): Response<Unit>

    @GET("/rest/v1/user")
    suspend fun getUser(
        @Query("userName") userName: String,
        @Query("passwordHash") passwordHash: String
    ): List<SupabaseUser>

    @GET("/rest/v1/user")
    suspend fun getUserByName(
        @Query("email") userName: String,
    ): List<SupabaseUser>
}