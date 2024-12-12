package com.mikeapp.newideatodoapp.data.supabase

import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseUser
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseUserApi {
    @POST("/rest/v1/user")
    suspend fun createUser(
        @Body supabaseUser: SupabaseUser
    ): Response<Unit>

    @PATCH("/rest/v1/user")
    suspend fun updateLong(
        @Query("id") id: String,
        @Body partialUpdate: Map<String, Long>
    ): Response<Unit>

    @PATCH("/rest/v1/user")
    suspend fun updateInt(
        @Query("id") id: String,
        @Body partialUpdate: Map<String, Int>
    ): Response<Unit>

    @GET("/rest/v1/user")
    suspend fun getUser(
        @Query("userName") userName: String,
        @Query("passwordHash") passwordHash: String
    ): List<SupabaseUser>

    @GET("/rest/v1/user")
    suspend fun getUserByName(
        @Query("userName") userName: String,
    ): List<SupabaseUser>
}