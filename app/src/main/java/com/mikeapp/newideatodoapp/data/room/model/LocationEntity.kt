package com.mikeapp.newideatodoapp.data.room.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "location")
data class LocationEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val lat: Double,
    val lon: Double,
    val radius: Double,
    val version: Long = 0L
)
