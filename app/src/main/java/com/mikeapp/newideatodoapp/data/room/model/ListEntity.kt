package com.mikeapp.newideatodoapp.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list")
data class ListEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val location: Int?,
    val sort: String?,
    val type: String?,
    val version: Long = 0L
)