package com.mikeapp.newideatodoapp.data

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

class NetworkModule {
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(Interceptor { chain ->
            chain.proceed(chain.request())
        })
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Log request and response bodies
        })
        .build()
}