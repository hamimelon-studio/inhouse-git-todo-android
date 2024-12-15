package com.mikeapp.newideatodoapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mikeapp.newideatodoapp.data.room.dao.ListDao
import com.mikeapp.newideatodoapp.data.room.dao.LocationDao
import com.mikeapp.newideatodoapp.data.room.dao.TaskDao
import com.mikeapp.newideatodoapp.data.room.dao.TaskDraftDao
import com.mikeapp.newideatodoapp.data.room.dao.UserDao
import com.mikeapp.newideatodoapp.data.room.model.ListEntity
import com.mikeapp.newideatodoapp.data.room.model.LocationEntity
import com.mikeapp.newideatodoapp.data.room.model.TaskDraftEntity
import com.mikeapp.newideatodoapp.data.room.model.TaskEntity
import com.mikeapp.newideatodoapp.data.room.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        LocationEntity::class,
        TaskEntity::class,
        TaskDraftEntity::class,
        ListEntity::class,
    ], version = 1
)
abstract class TnnDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    abstract fun locationDao(): LocationDao

    abstract fun taskDao(): TaskDao

    abstract fun taskDraftDao(): TaskDraftDao

    abstract fun listDao(): ListDao
}