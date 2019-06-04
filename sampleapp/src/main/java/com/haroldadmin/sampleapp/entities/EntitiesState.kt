package com.haroldadmin.sampleapp.entities

import com.haroldadmin.sampleapp.repository.CountingEntity
import com.haroldadmin.vector.VectorState

data class EntitiesState(
    val entities: List<CountingEntity>? = null,
    val isLoading: Boolean = false
) : VectorState