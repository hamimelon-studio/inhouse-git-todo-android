package com.mikeapp.newideatodoapp.domain

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mikeapp.newideatodoapp.data.GithubOpenApiRepository
import com.mikeapp.newideatodoapp.domain.model.User

class UserLoginUseCase(private val repository: GithubOpenApiRepository) {
    suspend fun getUsers(): List<User> {
        val usersJson = repository.readFileContent(usersJsonPath)
        Log.d("bbbb", "usersJson: $usersJson")
        usersJson?.let {
            val userListType = object : TypeToken<List<User>>() {}.type
            val userList: List<User> = Gson().fromJson(usersJson, userListType)
            Log.d("bbbb", "userList: $userList")
            return userList
        }
        return emptyList()
    }

    companion object {
        private val usersJsonPath = "todoroot/users.json"
    }
}