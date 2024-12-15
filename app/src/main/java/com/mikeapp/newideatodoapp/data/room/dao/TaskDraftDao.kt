package com.mikeapp.newideatodoapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mikeapp.newideatodoapp.data.room.model.TaskDraftEntity
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity

@Dao
interface TaskDraftDao {
    @Query("SELECT * FROM taskDraft WHERE id = 1")
    suspend fun getDraft(): TaskDraftEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDraft(taskEntity: TaskDraftEntity)

    @Query("DELETE FROM taskDraft")
    suspend fun clear()
}