package com.haroldadmin.sampleapp.repository

import com.haroldadmin.sampleapp.CountingEntity
import com.haroldadmin.sampleapp.CountingEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EntitiesRepository(private val dao: CountingEntityQueries) {

    suspend fun getAllEntities(): List<CountingEntity> = withContext(Dispatchers.IO) {
        dao.getAll().executeAsList()
    }

    suspend fun getCounterForEntity(name: String): Long = withContext(Dispatchers.IO) {
        dao.getCounterForEntity(name).executeAsOne()
    }

    suspend fun saveNewEntity(entity: CountingEntity) = withContext(Dispatchers.IO){
        dao.insert(entity.name, entity.counter, entity.colour)
    }

    suspend fun deleteEntity(entity: CountingEntity) = withContext(Dispatchers.IO){
        dao.delete(entity.name)
    }

    suspend fun updateEntity(entity: CountingEntity) = withContext(Dispatchers.IO){
        dao.update(entity.counter, entity.name)
    }
}