package com.mikeapp.newideatodoapp.data.datasource.github

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

private const val owner = "hongwei-bai"
private const val repo = "inhouse-git-todo-api"

interface GithubOpenApiService {
    @PUT("repos/$owner/$repo/contents/{path}")
    suspend fun createOrUpdateFile(
        @Path("path") path: String,
        @Body requestBody: GitHubFileRequest
    ): Response<GitHubApiFileContent>

    @GET("repos/$owner/$repo/contents/{path}")
    suspend fun getFileContent(@Path("path") path: String): Response<GitHubApiFileContent>

    @GET("repos/$owner/$repo/contents/{path}")
    suspend fun getFileMetadata(
        @Path("path") path: String
    ): Response<GitHubApiFileContent>
}