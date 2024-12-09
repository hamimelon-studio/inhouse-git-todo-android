package com.mikeapp.newideatodoapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mikeapp.newideatodoapp.data.room.dao.LocationDao
import com.mikeapp.newideatodoapp.data.room.dao.TaskDao
import com.mikeapp.newideatodoapp.data.room.dao.UserDao
import com.mikeapp.newideatodoapp.data.room.model.LocationEntity
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity
import com.mikeapp.newideatodoapp.data.room.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        LocationEntity::class,
        TaskEntity::class
    ], version = 1
)
abstract class TnnDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun locationDao(): LocationDao

    abstract fun taskDao(): TaskDao
}