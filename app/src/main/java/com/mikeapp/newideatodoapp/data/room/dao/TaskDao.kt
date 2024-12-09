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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(taskEntity: TaskEntity)

    @Query("DELETE FROM task WHERE id = :id")
    suspend fun remove(id: Int)

    @Query("DELETE FROM task")
    suspend fun clear()
}