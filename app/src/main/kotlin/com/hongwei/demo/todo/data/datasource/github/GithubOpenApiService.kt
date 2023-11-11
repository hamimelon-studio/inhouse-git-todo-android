package com.hongwei.demo.todo.data.datasource.github

import com.hongwei.demo.todo.data.datasource.github.Endpoint.FUEL_APP_MAIN_ENDPOINT
import com.hongwei.demo.todo.data.datasource.github.Endpoint.FUEL_CARD_IMAGE_TEMPLATE_ENDPOINT
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubOpenApiService {
    @GET(FUEL_APP_MAIN_ENDPOINT)
    suspend fun getFuelAppData(): Response<GithubOpenApiResponse>

    @GET(FUEL_CARD_IMAGE_TEMPLATE_ENDPOINT)
    suspend fun getFuelCardImage(@Path("id") id: String): Response<GithubOpenApiResponse>
}