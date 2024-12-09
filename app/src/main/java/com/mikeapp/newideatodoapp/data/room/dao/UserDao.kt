package com.mikeapp.newideatodoapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mikeapp.newideatodoapp.data.room.model.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getUser(): List<UserEntity>

    @Query("SELECT * FROM user WHERE userName = :userName AND passwordHash = :passwordHash")
    suspend fun getUserByCredential(userName: String, passwordHash: String): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(userEntity: UserEntity)

    @Query("DELETE FROM user WHERE id = :id")
    suspend fun remove(id: Int)

    @Query("DELETE FROM user")
    suspend fun clear()
}