package com.mikeapp.newideatodoapp.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mikeapp.newideatodoapp.data.room.model.ListEntity
import com.mikeapp.newideatodoapp.data.room.model.LocationEntity

@Dao
interface LocationDao {
    @Query("SELECT * FROM location WHERE id = :id")
    suspend fun getLocation(id: Int): LocationEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(locationEntity: LocationEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(locations: List<LocationEntity>)

    @Query("DELETE FROM location WHERE id = :id")
    suspend fun remove(id: Int)

    @Query("DELETE FROM location")
    suspend fun clear()
}