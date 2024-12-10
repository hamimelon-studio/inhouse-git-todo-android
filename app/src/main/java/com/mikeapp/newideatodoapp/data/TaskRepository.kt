package com.mikeapp.newideatodoapp.data

import android.util.Log
import com.mikeapp.newideatodoapp.Constant.logTag
import com.mikeapp.newideatodoapp.data.room.TnnDatabase
import com.mikeapp.newideatodoapp.data.room.model.ListEntity
import com.mikeapp.newideatodoapp.data.supabase.SupabaseNetworkModule

class TaskRepository(
    private val room: TnnDatabase,
    networkModule: SupabaseNetworkModule
) {
    private val versionApi = networkModule.supabaseVersionApi
    private val listApi = networkModule.supabaseListApi
    private val taskApi = networkModule.supabaseTaskApi

    suspend fun getLists(): List<ListEntity>? {
        val localUser = room.userDao().getUser().firstOrNull()
        if (localUser == null) {
            Log.e(logTag, "localUser is null, this error should never be raised. please check the code.")
            return null
        }

        val userId = localUser.id
        val version = versionApi.getVersion(eq(userId)).firstOrNull()
        val localVersion = room.versionDao().getVersion(userId)
        if (localVersion == null || localVersion.listVersion < (version?.list ?: 0)) {
            val lists = listApi.getList(eq(userId))
            room.listDao().clear()
            val listEntities = lists.map {
                ListEntity(
                    id = it.id ?: 0,
                    name = it.name,
                    location = it.location,
                    sort = it.sort,
                    type = it.type
                )
            }
            listEntities.forEach {
                room.listDao().save(it)
            }
            room.versionDao().updateListVersion(userId, version?.list ?: 0)
            return listEntities
        }
        return emptyList()
    }

    private fun eq(value: String): String = "eq.$value"

    private fun eq(value: Int): String = "eq.$value"
}