package com.mikeapp.newideatodoapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mikeapp.newideatodoapp.data.room.dao.ListDao
import com.mikeapp.newideatodoapp.data.room.dao.LocationDao
import com.mikeapp.newideatodoapp.data.room.dao.TaskDao
import com.mikeapp.newideatodoapp.data.room.dao.UserDao
import com.mikeapp.newideatodoapp.data.room.dao.VersionDao
import com.mikeapp.newideatodoapp.data.room.model.ListEntity
import com.mikeapp.newideatodoapp.data.room.model.LocationEntity
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity
import com.mikeapp.newideatodoapp.data.room.model.UserEntity
import com.mikeapp.newideatodoapp.data.room.model.VersionEntity

@Database(
    entities = [
        UserEntity::class,
        LocationEntity::class,
        TaskEntity::class,
        ListEntity::class,
        VersionEntity::class
    ], version = 1
)
abstract class TnnDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun locationDao(): LocationDao

    abstract fun taskDao(): TaskDao

    abstract fun listDao(): ListDao

    abstract fun versionDao(): VersionDao
}