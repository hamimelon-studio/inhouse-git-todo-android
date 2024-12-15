package com.mikeapp.newideatodoapp.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "taskDraft")
data class TaskDraftEntity(
    @PrimaryKey
    val id: Int = 1,
    val taskId: Int?,
    val name: String,
    val completed: Boolean,
    val location: Int?,
    val priority: Int,
    val due: String?,
    val time: String?,
    val list: Int
)
