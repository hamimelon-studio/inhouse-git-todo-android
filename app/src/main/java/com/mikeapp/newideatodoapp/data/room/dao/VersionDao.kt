package com.mikeapp.newideatodoapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mikeapp.newideatodoapp.data.room.model.VersionEntity

@Dao
interface VersionDao {
    @Query("SELECT * FROM version WHERE id = :id")
    suspend fun getVersion(id: Int): VersionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(versionEntity: VersionEntity)

    @Query("DELETE FROM version WHERE id = :id")
    suspend fun remove(id: Int)

    @Query("DELETE FROM version")
    suspend fun clear()

    @Query("UPDATE version SET locationVersion = :locationVersion WHERE id = :id")
    suspend fun updateLocationVersion(id: Int, locationVersion: Long)

    @Query("UPDATE version SET listVersion = :listVersion WHERE id = :id")
    suspend fun updateListVersion(id: Int, listVersion: Long)

    @Query("UPDATE version SET taskVersion = :taskVersion WHERE id = :id")
    suspend fun updateTaskVersion(id: Int, taskVersion: Long)
}