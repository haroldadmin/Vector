package com.haroldadmin.sampleapp.repository

import com.haroldadmin.sampleapp.CountingEntity
import com.haroldadmin.sampleapp.CountingEntityQueries
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class EntitiesRepository(private val dao: CountingEntityQueries) {

    suspend fun getAllEntities(): List<CountingEntity> = withContext(Dispatchers.IO) {
        dao.getAll().executeAsList()
    }

    suspend fun getEntity(id: String): CountingEntity = withContext(Dispatchers.IO) {
        dao.getEntity(id).executeAsOne()
    }

    suspend fun getCounterForEntity(id: String): Long = withContext(Dispatchers.IO) {
        dao.getCounterForEntity(id).executeAsOne()
    }

    suspend fun saveNewEntity(entity: CountingEntity) = withContext(Dispatchers.IO) {
        dao.insert(entity.id, entity.name, entity.counter, entity.colour)
    }

    suspend fun deleteEntity(entity: CountingEntity) = withContext(Dispatchers.IO) {
        dao.delete(entity.id)
    }

    suspend fun updateEntity(entity: CountingEntity) = withContext(Dispatchers.IO) {
        dao.update(entity.counter, entity.name, entity.colour, entity.id)
    }
}