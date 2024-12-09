package com.mikeapp.newideatodoapp.data.supabase

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

class SupabaseNetworkModule {
    companion object {
        const val SUPABASE_HEADER_API_KEY = "apiKey"
        const val SUPABASE_HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_TOKEN_SUPABASE = "Bearer ${SupabaseConfig.API_KEY}"
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val originalRequest: Request = chain.request()
            val requestWithAuthorization: Request = originalRequest.newBuilder()
                .addHeader(SUPABASE_HEADER_API_KEY, SupabaseConfig.API_KEY)
                .addHeader(SUPABASE_HEADER_AUTHORIZATION, BEARER_TOKEN_SUPABASE)
                .build()
            chain.proceed(requestWithAuthorization)
        })
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(SupabaseConfig.API_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val supabaseTaskApi = retrofit.create(SupabaseTaskApi::class.java)
    val supabaseUserApi = retrofit.create(SupabaseUserApi::class.java)
    val supabaseLocationApi = retrofit.create(SupabaseLocationApi::class.java)
}