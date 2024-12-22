package com.mikeapp.newideatodoapp.data.supabase

import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseTask
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface SupabaseTaskApi {
    @GET("/rest/v1/task")
    suspend fun getTasks(
        @Query("list") list: String
    ): List<SupabaseTask>

    @GET("/rest/v1/task")
    suspend fun getTaskById(
        @Query("id") taskId: String
    ): List<SupabaseTask>

    @GET("/rest/v1/task")
    suspend fun getTaskByName(
        @Query("name") name: String,
        @Query("list") listId: String
    ): List<SupabaseTask>

    @POST("/rest/v1/task")
    suspend fun insertTask(
        @Body supabaseTask: SupabaseTask
    ): Response<Unit>

    @PUT("/rest/v1/task")
    suspend fun updateTask(
        @Body supabaseTask: SupabaseTask
    ): Response<Unit>

    @DELETE("/rest/v1/task")
    suspend fun deleteTask(
        @Query("id") taskId: Int
    ): Response<Unit>
}