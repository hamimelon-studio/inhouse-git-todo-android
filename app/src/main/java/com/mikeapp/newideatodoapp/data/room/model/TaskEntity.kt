package com.mikeapp.newideatodoapp.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "task")
data class TaskEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String,
    val completed: Boolean,
    val location: Int?,
    val priority: Int,
    val due: String?,
    val time: String?,
    val list: Int,
    val locationNotification: Boolean,
    val dateTimeNotification: Boolean,
)
