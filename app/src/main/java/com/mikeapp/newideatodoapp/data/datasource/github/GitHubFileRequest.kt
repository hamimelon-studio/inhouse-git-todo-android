package com.mikeapp.newideatodoapp.data.datasource.github

data class GitHubFileRequest(
    val message: String,
    val content: String,  // Content encoded in Base64
    val branch: String? = "main", // Optional: specify the branch
    val sha: String? = null
)