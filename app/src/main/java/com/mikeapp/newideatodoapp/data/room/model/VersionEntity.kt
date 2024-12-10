package com.mikeapp.newideatodoapp.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "version")
data class VersionEntity(
    @PrimaryKey
    val id: Int,
    val taskVersion: Long,
    val listVersion: Long,
    val locationVersion: Long
)