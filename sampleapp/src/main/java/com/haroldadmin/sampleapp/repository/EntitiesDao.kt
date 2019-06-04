package com.haroldadmin.sampleapp.repository

import androidx.room.*

@Dao
interface EntitiesDao {

    @Query("SELECT counter FROM entities WHERE name = :name")
    suspend fun getCounterForEntity(name: String): Int?

    @Query("SELECT * FROM entities")
    suspend fun getAllEntities(): List<CountingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEntity(entity: CountingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addEntities(entities: List<CountingEntity>)

    @Update
    suspend fun updateEntity(entity: CountingEntity)

    @Delete
    suspend fun deleteEntity(entity: CountingEntity)

}