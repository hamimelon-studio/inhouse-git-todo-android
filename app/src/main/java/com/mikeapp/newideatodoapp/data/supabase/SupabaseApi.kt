package com.mikeapp.newideatodoapp.data.supabase

import com.mikeapp.newideatodoapp.data.supabase.model.Task
import retrofit2.http.GET
import retrofit2.http.Header

interface SupabaseApi {
    @GET("/rest/v1/tasks")  // Replace 'tasks' with your table name
    suspend fun getTasks(
        @Header("apikey") apiKey: String = SupabaseConfig.API_KEY,
        @Header("Authorization") authKey: String = "Bearer ${SupabaseConfig.API_KEY}"
    ): List<Task>
}