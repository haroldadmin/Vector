package com.haroldadmin.sampleapp.repository

class EntitiesRepository(private val dao: EntitiesDao) {

    suspend fun getAllEntities(): List<CountingEntity> {
        return dao.getAllEntities()
    }

    suspend fun getCounterForEntity(name: String): Int? {
        return dao.getCounterForEntity(name)
    }

    suspend fun saveNewEntity(entity: CountingEntity) {
        dao.addEntity(entity)
    }

    suspend fun deleteEntity(entity: CountingEntity) {
        dao.deleteEntity(entity)
    }

    suspend fun updateEntity(entity: CountingEntity) {
        dao.updateEntity(entity)
    }
}