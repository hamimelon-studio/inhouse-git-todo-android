package com.mikeapp.newideatodoapp.data

import com.mikeapp.newideatodoapp.data.exception.BackendAppException
import com.mikeapp.newideatodoapp.data.exception.CodeLogicException
import com.mikeapp.newideatodoapp.data.room.TnnDatabase
import com.mikeapp.newideatodoapp.data.room.model.ListEntity
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity
import com.mikeapp.newideatodoapp.data.room.model.UserEntity
import com.mikeapp.newideatodoapp.data.supabase.SupabaseNetworkModule
import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseList
import com.mikeapp.newideatodoapp.data.supabase.model.SupabaseTask

class TaskRepository(
    private val room: TnnDatabase,
    networkModule: SupabaseNetworkModule
) {
    private val listApi = networkModule.supabaseListApi
    private val taskApi = networkModule.supabaseTaskApi

    suspend fun addTask(taskName: String, listId: Int? = null) {
        val localList = if (listId != null) {
            getLocalList(listId)
        } else {
            getDefaultList()
        }

        addTaskToCloud(taskName, localList.id)
        val task = fetchTaskFromCloud(taskName, localList.id)
        val taskEntity = getTaskEntity(task)
        saveTaskToLocalRoomDb(taskEntity)
        updateTaskVersion(localList.id)
    }

    suspend fun updateTask(taskId: Int, taskName: String) {
        val taskEntity = getTaskFromRoomDb(taskId)
        val taskEntityNew = taskEntity.copy(
            name = taskName
        )
        room.taskDao().save(taskEntityNew)
        val task = mapToTask(taskEntityNew)
        taskApi.updateTask(task)
        updateTaskVersion(taskEntityNew.list)
    }

    private fun mapToTask(taskEntity: TaskEntity): SupabaseTask {
        return SupabaseTask(
            id = taskEntity.id,
            name = taskEntity.name,
            completed = taskEntity.completed,
            location = taskEntity.location,
            priority = taskEntity.priority,
            due = taskEntity.due,
            time = taskEntity.time,
            list = taskEntity.list
        )
    }

    suspend fun getUser(): UserEntity {
        return getUserFromRoomDb()
    }

    suspend fun getLists(): List<ListEntity> {
        return room.listDao().getLists()
    }

    suspend fun getList(listId: Int): ListEntity {
        return getLocalListById(listId)
    }

    suspend fun getTasks(listId: Int? = null): List<TaskEntity> {
        val localList = if (listId != null) {
            getLocalList(listId)
        } else {
            getDefaultList()
        }
        return room.taskDao().getTasks(localList.id)
    }

    suspend fun forceUpdateTasks(listId: Int? = null): List<TaskEntity> {
        val localList = if (listId != null) {
            getLocalList(listId)
        } else {
            getDefaultList()
        }
        val list = loadListByIdFromCloud(localList.id)
        if (localList.taskVersion < list.taskVersion) {
            val tasks = fetchTasksByListFromCloud(localList.id)
            val taskEntities = tasks.map { getTaskEntity(it) }
            room.taskDao().saveAll(taskEntities)
            return taskEntities
        } else {
            return room.taskDao().getTasks(localList.id)
        }
    }

    suspend fun getTask(taskId: Int): TaskEntity {
        return getTaskFromRoomDb(taskId)
    }

    private suspend fun getTaskFromRoomDb(taskId: Int): TaskEntity {
        return room.taskDao().getTask(taskId) ?: throw CodeLogicException("task $taskId is null")
    }

    private suspend fun getLocalListById(listId: Int): ListEntity {
        val listEntity = room.listDao().getList(listId) ?: throw CodeLogicException("list $listId is null")
        return listEntity
    }

    private suspend fun getLocalTasks(listId: Int): List<TaskEntity> {
        return room.taskDao().getTasks(listId)
    }

    private suspend fun updateTaskVersion(listId: Int) {
        val timeStamp = System.currentTimeMillis()
        val partialUpdate = mapOf("taskVersion" to timeStamp)
        val response = listApi.update(eq(listId), partialUpdate)
        if (!response.isSuccessful) {
            throw BackendAppException("update task version failed")
        }
        room.listDao().updateTaskVersion(listId, timeStamp)
    }

    private suspend fun saveTaskToLocalRoomDb(taskEntity: TaskEntity) {
        room.taskDao().save(taskEntity)
    }

    private fun getTaskEntity(task: SupabaseTask): TaskEntity {
        if (task.id == null) throw BackendAppException("task id is null from cloud.")
        return TaskEntity(
            id = task.id,
            name = task.name,
            completed = task.completed,
            location = task.location,
            priority = task.priority,
            due = task.due,
            time = task.time,
            list = task.list
        )
    }

    private suspend fun fetchTasksByListFromCloud(listId: Int): List<SupabaseTask> {
        return taskApi.getTasks(eq(listId))
    }

    private suspend fun fetchTaskFromCloud(taskName: String, listId: Int): SupabaseTask {
        val tasks = taskApi.getTaskByName(eq(taskName), eq(listId))
        if (tasks.isEmpty()) {
            throw BackendAppException("fetch task from supabase failed")
        }
        return tasks.first()
    }

    private suspend fun addTaskToCloud(taskName: String, listId: Int) {
        val task = SupabaseTask(
            name = taskName,
            completed = false,
            location = null,
            priority = 1,
            due = null,
            time = null,
            list = listId
        )
        val response = taskApi.insertTask(task)
        if (!response.isSuccessful) {
            throw BackendAppException("insert new task to supabase failed")
        }
    }

    private suspend fun getLocalList(listId: Int): ListEntity {
        return room.listDao().getList(listId) ?: throw CodeLogicException("localList is null")
    }

    private suspend fun getDefaultList(): ListEntity {
        return room.listDao().getListByName("Default") ?: throw CodeLogicException("localList is null")
    }

    private suspend fun getLocalListsFromRoomDb(): List<ListEntity> {
        val listEntities = room.listDao().getLists()
        if (listEntities.isEmpty()) throw CodeLogicException("list entities is empty, there should be at least a default list.")
        return listEntities
    }

    private fun getLocalLists(lists: List<SupabaseList>): List<ListEntity> {
        val listEntities = lists.map {
            ListEntity(
                id = it.id ?: 0,
                name = it.name,
                location = it.location,
                sort = it.sort,
                taskVersion = it.taskVersion
            )
        }
        return listEntities
    }

    private suspend fun updateLocalLists(listEntities: List<ListEntity>) {
        room.listDao().clear()
        room.listDao().saveAll(listEntities)
    }

    private suspend fun loadListByIdFromCloud(userId: Int): SupabaseList {
        val lists = listApi.getList(eq(userId))
        if (lists.isEmpty()) throw BackendAppException("fetch lists from supabase get empty results")
        return lists.first()
    }

    private suspend fun loadListsFromCloud(userId: Int): List<SupabaseList> {
        val lists = listApi.getLists(eq(userId))
        if (lists.isEmpty()) throw BackendAppException("fetch lists from supabase get empty results")
        return lists
    }

    private suspend fun getUserIdFromRoomDb(): Int {
        return getUserFromRoomDb().id
    }

    private suspend fun getUserFromRoomDb(): UserEntity {
        val localUser = room.userDao().getUser().firstOrNull()
            ?: throw CodeLogicException("localUser is null, this error should never be raised. please check the code.")
        return localUser
    }

    private fun eq(value: String): String = "eq.$value"

    private fun eq(value: Int): String = "eq.$value"
}