package com.mikeapp.newideatodoapp.data

import android.util.Log
import com.mikeapp.newideatodoapp.Constant.logTag
import com.mikeapp.newideatodoapp.data.enums.UserTypeTier
import com.mikeapp.newideatodoapp.data.room.TnnDatabase
import com.mikeapp.newideatodoapp.data.room.model.UserEntity
import com.mikeapp.newideatodoapp.data.supabase.SupabaseNetworkModule
import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseUser
import com.mikeapp.newideatodoapp.util.SecurityUtil
import org.koin.java.KoinJavaComponent.get

class UserRepository(
    private val room: TnnDatabase,
    networkModule: SupabaseNetworkModule
) {
    private val securityUtil = get<SecurityUtil>(SecurityUtil::class.java)
    private val userApi = networkModule.supabaseUserApi

    suspend fun retrieveLocalUser(): UserEntity? {
        val users = room.userDao().getUser()
        val user = users.firstOrNull()
        return user
    }

    suspend fun createNewAccount(userName: String, email: String, password: String, nickName: String): UserEntity? {
        val users = userApi.getUserByName(eq(userName))
        if (users.isNotEmpty()) {
            return null
        }

        val supabaseUser = SupabaseUser(
            userName = userName,
            email = email,
            passwordHash = securityUtil.hashPassword(password),
            type = UserTypeTier.FreeTier.name,
            nickName = nickName
        )
        val response = userApi.createUser(supabaseUser)
        if (!response.isSuccessful) {
            Log.e(
                logTag,
                "create user api failed: response.code: ${response.code()}, error: ${
                    response.errorBody().toString()
                }"
            )
            return null
        }

        val response2 = userApi.getUserByName(eq(email))
        if (response2.isEmpty()) {
            Log.e(logTag, "user not created")
            return null
        }

        val user = response2.first()

        if (user.id == null || user.created_at == null) {
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
            rememberMe = true,
            nickName = user.nickName.ifEmpty { user.userName }
        )
        updateUser(roomUserEntity)
        return roomUserEntity
    }

    suspend fun authenticateUser(userName: String, password: String, rememberMe: Boolean): UserEntity? {
        val passwordHash = securityUtil.hashPassword(password)
        val response = userApi.getUser(eq(userName), eq(passwordHash))
        Log.d(logTag, "response: $response")

        val userEntity = if (response.isNotEmpty()) {
            val user = response.first()
            UserEntity(
                id = user.id ?: 0,
                created_at = user.created_at ?: "",
                userName = user.userName,
                passwordHash = user.passwordHash,
                type = user.type,
                email = user.email,
                rememberMe = rememberMe,
                nickName = user.nickName
            )
        } else null
        userEntity?.let {
            updateUser(userEntity)
        }
        return userEntity
    }

    private suspend fun updateUser(userEntity: UserEntity) {
        val localUser = room.userDao().getUser().firstOrNull()
        if (localUser?.id == userEntity.id) {
            room.userDao().save(userEntity)
        } else {
            room.userDao().clear()
            room.listDao().clear()
            room.taskDao().clear()
            room.locationDao().clear()
            room.versionDao().clear()
            room.userDao().save(userEntity)
        }
    }

    suspend fun authenticateUserHash(userName: String, passwordHash: String): SupabaseUser? {
        val response = userApi.getUser(eq(userName), eq(passwordHash))
        Log.d(logTag, "response: $response")

        return response.firstOrNull()
    }

    private fun eq(value: String): String = "eq.$value"
}