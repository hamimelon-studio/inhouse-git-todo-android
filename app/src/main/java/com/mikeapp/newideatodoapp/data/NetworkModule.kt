package com.mikeapp.newideatodoapp.data

import com.mikeapp.newideatodoapp.BuildConfig
import com.mikeapp.newideatodoapp.data.datasource.GithubApiService
import com.mikeapp.newideatodoapp.data.supabase.SupabaseApi
import com.mikeapp.newideatodoapp.data.supabase.SupabaseConfig
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor

object NetworkModule {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val originalRequest: Request = chain.request()
            val requestWithAuthorization: Request = originalRequest.newBuilder()
                .header(AUTHORIZATION_HEADER, BEARER_TOKEN)
                .build()
            chain.proceed(requestWithAuthorization)
        })
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
        })
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val githubApiService = retrofit.create(GithubApiService::class.java)

    const val AUTHORIZATION_HEADER = "Authorization"
    const val BEARER_TOKEN = "Bearer ${BuildConfig.STATIC_API_TOKEN}"

    const val AUTHORIZATION_HEADER_SUPABASE = "Authorization"
    const val BEARER_TOKEN_SUPABASE = "Bearer ${SupabaseConfig.API_KEY}"

    private val okHttpClientForSupabase: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            val originalRequest: Request = chain.request()
            val requestWithAuthorization: Request = originalRequest.newBuilder()
                .header(AUTHORIZATION_HEADER_SUPABASE, BEARER_TOKEN_SUPABASE)
                .build()
            chain.proceed(requestWithAuthorization)
        })
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
        })
        .build()

    private val retrofitSupabase = Retrofit.Builder()
        .baseUrl(SupabaseConfig.API_URL)
        .client(okHttpClientForSupabase)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val supabaseApi = retrofitSupabase.create(SupabaseApi::class.java)
}