package com.mikeapp.newideatodoapp.data

import android.util.Log
import com.mikeapp.newideatodoapp.Constant.logTag
import com.mikeapp.newideatodoapp.data.room.TnnDatabase
import com.mikeapp.newideatodoapp.data.room.model.UserEntity
import com.mikeapp.newideatodoapp.data.supabase.SupabaseNetworkModule
import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseUser
import com.mikeapp.newideatodoapp.data.supabase.model.UserTypeTier
import com.mikeapp.newideatodoapp.util.SecurityUtil
import org.koin.java.KoinJavaComponent.get

class SupabaseRepository(
    private val room: TnnDatabase,
    networkModule: SupabaseNetworkModule
) {
    private val securityUtil = get<SecurityUtil>(SecurityUtil::class.java)
    private val supabaseUserApi = networkModule.supabaseUserApi

    suspend fun retrieveLocalUser(): UserEntity? {
        val users = room.userDao().getUser()
        if (users.size > 1) {
            Log.w(logTag, "users in room db should be always 1, now size is: ${users.size}")
        }

        val user = users.firstOrNull()
        return user
    }

    suspend fun createNewAccount(userName: String, email: String, password: String): UserEntity? {
        val users = supabaseUserApi.getUserByEmail(eq(email))
        if (users.isNotEmpty()) {
            return null
        }

        val supabaseUser = SupabaseUser(
            userName = userName,
            email = email,
            passwordHash = securityUtil.hashPassword(password),
            type = UserTypeTier.FreeTier.name
        )
        val response = supabaseUserApi.createUser(supabaseUser)
        if (!response.isSuccessful) {
            Log.e(
                logTag,
                "create user api failed: response.code: ${response.code()}, error: ${
                    response.errorBody().toString()
                }"
            )
            return null
        }

        val response2 = supabaseUserApi.getUserByEmail(eq(email))
        if (response2.isEmpty()) {
            Log.e(logTag, "user not created")
            return null
        }

        val user = response2.first()

        if(user.id == null || user.created_at == null) {
            Log.e(logTag, "user id or created_at is null")
            return null
        }

        val roomUserEntity = UserEntity(
            id = user.id,
            created_at = user.created_at,
            userName = user.userName,
            email = user.email,
            passwordHash = user.passwordHash,
            type = user.type,
            rememberMe = true
        )
        room.userDao().save(roomUserEntity)
        return roomUserEntity
    }

    suspend fun authenticateUser(userName: String, password: String): SupabaseUser? {
        val passwordHash = securityUtil.hashPassword(password)
        val response = supabaseUserApi.getUser(eq(userName), eq(passwordHash))
        Log.d(logTag, "response: $response")

        return response.firstOrNull()
    }

    suspend fun authenticateUserHash(userName: String, passwordHash: String): SupabaseUser? {
        val response = supabaseUserApi.getUser(eq(userName), eq(passwordHash))
        Log.d(logTag, "response: $response")

        return response.firstOrNull()
    }

    private fun eq(value: String): String = "eq.$value"
}