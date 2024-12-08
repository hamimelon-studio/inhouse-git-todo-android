package com.mikeapp.newideatodoapp.data.supabase

import com.mikeapp.newideatodoapp.data.supabase.model.Location
import com.mikeapp.newideatodoapp.data.supabase.model.Task
import com.mikeapp.newideatodoapp.data.supabase.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseTaskApi {
    @GET("/rest/v1/task")
    suspend fun getTask(
        @Query("user") user: Int
    ): List<Task>

    @POST("/rest/v1/task")
    suspend fun insertTask(
        @Body task: Task
    ): Response<Unit>

    @GET("/rest/v1/location")
    suspend fun getLocation(
        @Query("user") user: Int
    ): List<Location>
}