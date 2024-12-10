package com.mikeapp.newideatodoapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mikeapp.newideatodoapp.data.room.model.ListEntity

@Dao
interface ListDao {
    @Query("SELECT * FROM list WHERE id = :id")
    suspend fun getList(id: Int): ListEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(listEntity: ListEntity)

    @Query("DELETE FROM list WHERE id = :id")
    suspend fun remove(id: Int)

    @Query("DELETE FROM list")
    suspend fun clear()
}