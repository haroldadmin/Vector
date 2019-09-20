package com.haroldadmin.sampleapp.entities

import androidx.lifecycle.viewModelScope
import com.haroldadmin.sampleapp.repository.EntitiesRepository
import com.haroldadmin.vector.VectorViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

class EntitiesViewModel @AssistedInject constructor(
    @Assisted initialState: EntitiesState,
    private val repository: EntitiesRepository
) : VectorViewModel<EntitiesState>(initialState) {

    fun getAllEntities() = viewModelScope.launch {
        val entities = repository.getAllEntities()
        setState {
            val newState = copy(entities = entities, isLoading = false)
            newState
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: EntitiesState): EntitiesViewModel
    }
}
