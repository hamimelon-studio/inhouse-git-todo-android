package com.mikeapp.newideatodoapp.data.datasource

import com.mikeapp.newideatodoapp.data.datasource.RepoConfig.owner
import com.mikeapp.newideatodoapp.data.datasource.RepoConfig.repo
import com.mikeapp.newideatodoapp.data.datasource.model.GitHubFileRequest
import com.mikeapp.newideatodoapp.data.datasource.model.GithubFileContent
import com.mikeapp.newideatodoapp.data.datasource.model.GithubFileMetadata
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface GithubApiService {
    // create file doesn't need sha if file isn't exist
    @PUT("repos/$owner/$repo/contents/{path}")
    suspend fun createFile(
        @Path("path")path: String,
        @Body requestBody: GitHubFileRequest
    ): Response<String>

    // sha must be provided for updating existing file
    @PUT("repos/$owner/$repo/contents/{path}")
    suspend fun updateFile(
        @Path("path")path: String,
        @Body requestBody: GitHubFileRequest
    ): Response<String>

    // get 404 if file isn't exist
    // get 200 and its metadata if exist
    @GET("repos/$owner/$repo/contents/{path}")
    suspend fun getFileMetadata(
        @Path("path") path: String
    ): Response<GithubFileMetadata>

    @GET("repos/$owner/$repo/contents/{path}")
    suspend fun getFileContent(@Path("path") path: String): Response<GithubFileContent>
}