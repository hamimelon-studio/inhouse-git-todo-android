package com.mikeapp.newideatodoapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity

@Dao
interface TaskDao {
    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getTask(id: Int): TaskEntity?

    @Query("SELECT * FROM task WHERE list = :listId")
    suspend fun getTasks(listId: Int): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(taskEntity: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(tasks: List<TaskEntity>)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun remove(id: Int)

    @Query("DELETE FROM task")
    suspend fun clear()
}