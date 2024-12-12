package com.mikeapp.newideatodoapp.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    val id: Int,
    val created_at: String,
    val userName: String,
    val passwordHash: String,
    val type: String,
    val email: String,
    val rememberMe: Boolean,
    val nickName: String,
    val defaultList: Int,
    val listVersion: Long,
    val locationVersion: Long
)