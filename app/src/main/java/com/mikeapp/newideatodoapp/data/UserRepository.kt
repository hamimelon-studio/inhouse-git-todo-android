package com.mikeapp.newideatodoapp.data

import android.util.Log
import com.mikeapp.newideatodoapp.Constant.logTag
import com.mikeapp.newideatodoapp.data.enums.UserTypeTier
import com.mikeapp.newideatodoapp.data.exception.AppException
import com.mikeapp.newideatodoapp.data.exception.BackendAppException
import com.mikeapp.newideatodoapp.data.exception.CodeLogicException
import com.mikeapp.newideatodoapp.data.room.TnnDatabase
import com.mikeapp.newideatodoapp.data.room.model.ListEntity
import com.mikeapp.newideatodoapp.data.room.model.LocationEntity
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity
import com.mikeapp.newideatodoapp.data.room.model.UserEntity
import com.mikeapp.newideatodoapp.data.supabase.SupabaseNetworkModule
import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseList
import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseUser
import com.mikeapp.newideatodoapp.util.SecurityUtil
import org.koin.java.KoinJavaComponent.get

class UserRepository(
    private val room: TnnDatabase,
    networkModule: SupabaseNetworkModule
) {
    private val securityUtil = get<SecurityUtil>(SecurityUtil::class.java)
    private val userApi = networkModule.supabaseUserApi
    private val listApi = networkModule.supabaseListApi
    private val taskApi = networkModule.supabaseTaskApi
    private val locationApi = networkModule.supabaseLocationApi

    suspend fun retrieveLocalUser(): UserEntity? {
        val users = room.userDao().getUser()
        val user = users.firstOrNull()
        return user
    }

    suspend fun createNewAccount(userName: String, email: String, password: String, nickName: String) {
        checkUserNameExistence(userName)
        createUserOnSupabase(userName, email, password, nickName)
        val user = loadUserFromSupabase(userName)
        if (user.id == null) throw CodeLogicException("user id is null")

        val userEntity = getUserEntity(user)
        val localUserEntity = getLocalUserIdNullable()
        if (localUserEntity != null && localUserEntity.id == userEntity.id) {
            updateUser(userEntity)
        } else {
            clearLocalUser()
            setupNewLocalUser(userEntity)
        }

        createDefaultListForNewUser(user.id)
        val defaultList = getDefaultList(user.id)
        val defaultListEntity = getListEntity(defaultList)
        saveDefaultListToRoomDb(defaultListEntity)

        updateListVersion(user.id, 0L)
        updateDefaultList(user.id, defaultListEntity.id)
    }

    suspend fun authenticateUser(userName: String, password: String, rememberMe: Boolean) {
        val passwordHash = securityUtil.hashPassword(password)
        val user = loadUserFromBackendByCredentials(userName, passwordHash)
        val userEntity = getUserEntity(user, rememberMe)
        val localUserEntity = getLocalUserIdNullable()
        if (localUserEntity != null && localUserEntity.id == userEntity.id) {
            updateUser(userEntity)
            incrementalUpdateLocation(user, userEntity)
            incrementalUpdateListAndTask(user, userEntity)
        } else {
            clearLocalUser()
            setupNewLocalUser(userEntity)
            downloadListAndTaskFromCloud(userEntity.id)
            downloadLocationFromCloud(userEntity.id)
        }
    }

    private suspend fun incrementalUpdateListAndTask(user: SupabaseUser, userEntity: UserEntity) {
        if (userEntity.listVersion < user.listVersion) {
            val lists = listApi.getLists(eq(userEntity.id))
            val listEntities = lists.map {
                if (it.id == null) throw BackendAppException("list id should never be null")
                ListEntity(
                    id = it.id,
                    name = it.name,
                    location = it.location,
                    sort = it.sort,
                    taskVersion = it.taskVersion
                )
            }
            room.listDao().saveAll(listEntities)

            lists.forEachIndexed { index, list ->
                val listEntity = listEntities[index]
                if (listEntity.taskVersion < list.taskVersion) {
                    downloadTaskFromCloud(listEntity.id)
                }
            }
        }
    }

    private suspend fun incrementalUpdateLocation(user: SupabaseUser, userEntity: UserEntity) {
        if (userEntity.locationVersion < user.locationVersion) {
            downloadLocationFromCloud(userEntity.id)
        }
    }

    private suspend fun downloadTaskFromCloud(listId: Int) {
        val tasks = taskApi.getTasks(eq(listId))
        val taskEntities = tasks.map {
            if (it.id == null) throw CodeLogicException("task id should never be null")
            TaskEntity(
                id = it.id,
                name = it.name,
                completed = it.completed,
                location = it.location,
                priority = it.priority,
                due = it.due,
                time = it.time,
                list = it.list,
            )
        }
        room.taskDao().saveAll(taskEntities)
    }

    private suspend fun downloadLocationFromCloud(userId: Int) {
        val locations = locationApi.getLocation(eq(userId))
        val locationEntities = locations.map {
            if (it.id == null) throw BackendAppException("list id should never be null")
            LocationEntity(
                id = it.id,
                name = it.name,
                lat = it.lat,
                lon = it.lon,
                radius = it.radius
            )
        }
        room.locationDao().saveAll(locationEntities)
    }

    private suspend fun downloadListAndTaskFromCloud(userId: Int) {
        val lists = listApi.getLists(eq(userId))
        val listEntities = lists.map {
            if (it.id == null) throw BackendAppException("list id should never be null")
            ListEntity(
                id = it.id,
                name = it.name,
                location = it.location,
                sort = it.sort,
                taskVersion = it.taskVersion
            )
        }
        room.listDao().saveAll(listEntities)

        listEntities.forEach {
            val tasks = taskApi.getTasks(eq(it.id))
            val taskEntities = tasks.map { task ->
                if (task.id == null) throw CodeLogicException("task id should never be null")
                TaskEntity(
                    id = task.id,
                    name = task.name,
                    completed = task.completed,
                    location = task.location,
                    priority = task.priority,
                    due = task.due,
                    time = task.time,
                    list = task.list,
                )
            }
            room.taskDao().saveAll(taskEntities)
        }
    }

    private suspend fun updateDefaultList(userId: Int, defaultListId: Int) {
        val partialUpdate = mapOf("defaultList" to defaultListId)
        userApi.updateInt(eq(userId), partialUpdate)
        room.userDao().updateDefaultList(userId, defaultListId)
    }

    private suspend fun updateListVersion(userId: Int, listVersion: Long) {
        val partialUpdate = mapOf("listVersion" to listVersion)
        userApi.updateLong(eq(userId), partialUpdate)
        room.userDao().updateListVersion(userId, listVersion)
    }

    private fun getUserEntity(user: SupabaseUser, rememberMe: Boolean = true): UserEntity {
        return UserEntity(
            id = user.id ?: 0,
            created_at = user.created_at ?: "",
            userName = user.userName,
            passwordHash = user.passwordHash,
            type = user.type,
            email = user.email,
            rememberMe = rememberMe,
            nickName = user.nickName,
            defaultList = user.defaultList,
            listVersion = user.listVersion,
            locationVersion = user.locationVersion
        )
    }

    private suspend fun loadUserFromBackendByCredentials(userName: String, passwordHash: String): SupabaseUser {
        val response = userApi.getUser(eq(userName), eq(passwordHash))
        if (response.isEmpty()) {
            throw BackendAppException("User name or password is incorrect, authenticate failed")
        }
        return response.first()
    }

    private fun getListEntity(list: SupabaseList): ListEntity {
        if (list.id == null) throw CodeLogicException("list id is null")
        val listEntity = ListEntity(
            id = list.id,
            name = list.name,
            location = list.location,
            sort = list.sort,
            taskVersion = list.taskVersion
        )
        return listEntity
    }

    private suspend fun saveDefaultListToRoomDb(listEntity: ListEntity) {
        room.listDao().save(listEntity)
    }

    private suspend fun getDefaultList(userId: Int): SupabaseList {
        val response = listApi.getLists(eq(userId))
        if (response.isEmpty()) {
            throw BackendAppException("default list not created")
        }
        return response.first()
    }

    private suspend fun createDefaultListForNewUser(userId: Int): Boolean {
        val defaultList = SupabaseList(
            name = "Default",
            location = null,
            sort = null,
            user = userId,
            taskVersion = 0
        )
        val response = listApi.insertList(defaultList)
        if (!response.isSuccessful) {
            Log.e(
                logTag,
                "create default list api failed: response.code: ${response.code()}, error: ${
                    response.errorBody().toString()
                }"
            )
            return false
        }
        return true
    }

    private suspend fun checkUserNameExistence(userName: String) {
        val users = userApi.getUserByName(eq(userName))
        if (users.isNotEmpty()) {
            throw AppException("Username already exists.")
        }
    }

    private suspend fun loadUserFromSupabase(userName: String): SupabaseUser {
        val response = userApi.getUserByName(eq(userName))
        if (response.isEmpty()) {
            throw BackendAppException("user not created")
        }

        val user = response.first()
        if (user.id == null || user.created_at == null) {
            throw BackendAppException("user id or created_at is null")
        }
        return user
    }

    private suspend fun createUserOnSupabase(
        userName: String,
        email: String,
        password: String,
        nickName: String
    ) {
        val supabaseUser = SupabaseUser(
            userName = userName,
            email = email,
            passwordHash = securityUtil.hashPassword(password),
            type = UserTypeTier.FreeTier.name,
            nickName = nickName,
            defaultList = -1,
            listVersion = 0,
            locationVersion = 0
        )
        val response = userApi.createUser(supabaseUser)
        if (!response.isSuccessful) {
            throw BackendAppException(
                "create user api failed: response.code: ${response.code()}, error: ${
                    response.errorBody().toString()
                }"
            )
        }
    }

    private suspend fun getLocalUserIdNullable(): UserEntity? {
        return room.userDao().getUser().firstOrNull()
    }

    private suspend fun updateUser(userEntity: UserEntity) {
        room.userDao().save(userEntity)
    }

    private suspend fun clearLocalUser() {
        room.userDao().clear()
        room.listDao().clear()
        room.taskDao().clear()
        room.locationDao().clear()
    }

    private suspend fun setupNewLocalUser(userEntity: UserEntity) {
        room.userDao().save(userEntity)
    }

    suspend fun authenticateUserHash(userName: String, passwordHash: String): SupabaseUser? {
        val response = userApi.getUser(eq(userName), eq(passwordHash))
        Log.d(logTag, "response: $response")

        return response.firstOrNull()
    }

    private fun eq(value: Int): String = "eq.$value"

    private fun eq(value: String): String = "eq.$value"
}