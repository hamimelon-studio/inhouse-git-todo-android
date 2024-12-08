package com.mikeapp.newideatodoapp.data.supabase

import com.mikeapp.newideatodoapp.data.supabase.model.Location
import com.mikeapp.newideatodoapp.data.supabase.model.Task
import com.mikeapp.newideatodoapp.data.supabase.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseApi {
    @GET("/rest/v1/user")
    suspend fun getUser(
        @Header("apikey") apiKey: String = SupabaseConfig.API_KEY,
        @Header("Authorization") authKey: String = "Bearer ${SupabaseConfig.API_KEY}",
        @Query("user") user: String
    ): List<User>

    @GET("/rest/v1/task")
    suspend fun getTask(
        @Header("apikey") apiKey: String = SupabaseConfig.API_KEY,
        @Header("Authorization") authKey: String = "Bearer ${SupabaseConfig.API_KEY}",
        @Query("user") user: Int
    ): List<Task>

    @POST("/rest/v1/task")
    suspend fun insertTask(
        @Header("apikey") apiKey: String = SupabaseConfig.API_KEY,
        @Header("Authorization") authKey: String = "Bearer ${SupabaseConfig.API_KEY}",
        @Body task: Task
    ): Response<Unit>

    @GET("/rest/v1/location")
    suspend fun getLocation(
        @Header("apikey") apiKey: String = SupabaseConfig.API_KEY,
        @Header("Authorization") authKey: String = "Bearer ${SupabaseConfig.API_KEY}",
        @Query("user") user: Int
    ): List<Location>
}