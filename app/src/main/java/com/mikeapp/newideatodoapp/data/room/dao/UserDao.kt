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

    @Query("UPDATE user SET defaultList = :defaultListId WHERE id = :userId")
    suspend fun updateDefaultList(userId: Int, defaultListId: Int)

    @Query("UPDATE user SET listVersion = :listVersion WHERE id = :userId")
    suspend fun updateListVersion(userId: Int, listVersion: Long)

    @Query("UPDATE user SET locationVersion = :locationVersion WHERE id = :userId")
    suspend fun updateLocationVersion(userId: Int, locationVersion: Long)

    @Query("DELETE FROM user WHERE id = :id")
    suspend fun remove(id: Int)

    @Query("DELETE FROM user")
    suspend fun clear()
}