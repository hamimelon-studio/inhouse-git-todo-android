package com.mikeapp.newideatodoapp.data.repository

import android.util.Log
import com.mikeapp.newideatodoapp.data.datasource.NetworkModule.githubApiService
import com.mikeapp.newideatodoapp.data.datasource.github.GitHubFileRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class GithubOpenApiRepository() {
    fun test() {
//        createFileOnGitHub(
//            "todoroot/test1.txt",
//            "good test string input good new changes!!!",
//            "test commit hahaha commit#2",
//        )
        readFileFromGithub("todoroot/test1.txt")
    }

    fun createFileOnGitHub(path: String, fileContent: String, commitMessage: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val fileMetadataResponse = githubApiService.getFileMetadata(path)
            Log.d("bbbb", "fileMetadataResponse: $fileMetadataResponse")
            if (fileMetadataResponse.isSuccessful || fileMetadataResponse.code() == 404) {
                val currentSha = if (fileMetadataResponse.isSuccessful) {
                    val fileMetadata = fileMetadataResponse.body()
                    Log.d("bbbb", "fileMetadata body: $fileMetadata")

                    // Ensure we have the current SHA
                    fileMetadata?.sha ?: throw IllegalStateException("File SHA not found")
                } else null

                // Encode the file content in Base64
                val encodedContent = android.util.Base64.encodeToString(
                    fileContent.toByteArray(Charsets.UTF_8),
                    android.util.Base64.DEFAULT
                )

                // Create the request body with the encoded content
                val createFileRequest = GitHubFileRequest(
                    message = commitMessage,
                    content = encodedContent,
                    sha = currentSha
                )

                // Make the API call to create the file
                val response = githubApiService.createOrUpdateFile(path, createFileRequest)

                if (response.isSuccessful) {
                    // File created successfully
                    val fileContent = response.body()
                    Log.d("GitHub API", "File created successfully: $fileContent")
                } else {
                    // Handle errors
                    Log.e("GitHub API", "Error creating file: ${response.code()}")

                    val errorBody = response.errorBody()?.string()
                    Log.e("GitHub API", "Error creating file: ${response.code()}, $errorBody")
                }
            }
        }
    }

    fun readFileFromGithub(path: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = githubApiService.getFileContent(path)

            if (response.isSuccessful) {
                val fileContent = response.body()
                if (fileContent != null) {

                    Log.d("bbbb", "content: $fileContent")

                    // Decode the Base64 content of the file
//                    val decodedContent = String(
//                        android.util.Base64.decode(
//                            fileContent.content,
//                            android.util.Base64.URL_SAFE
//                        )
//                    )
//
//                    Log.d("GitHub API", "decodedContent: $decodedContent")
                }
            } else {
                Log.e("GitHub API", "Error: ${response.code()}")
            }
        }
    }
}