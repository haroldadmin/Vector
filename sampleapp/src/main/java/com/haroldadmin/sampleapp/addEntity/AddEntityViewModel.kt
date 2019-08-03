package com.haroldadmin.sampleapp.addEntity

import androidx.lifecycle.viewModelScope
import com.haroldadmin.sampleapp.CountingEntity
import com.haroldadmin.sampleapp.repository.EntitiesRepository
import com.haroldadmin.sampleapp.repository.getRandomColour
import com.haroldadmin.vector.VectorViewModel
import kotlinx.coroutines.launch

class AddEntityViewModel(
    initState: AddEntityState?,
    private val entityRepository: EntitiesRepository
) : VectorViewModel<AddEntityState>(initState ?: AddEntityState()) {

    fun incrementCount() = viewModelScope.launch {
        setState { copy(count = this.count + 1) }
    }

    fun decrementCount() = viewModelScope.launch {
        setState { copy(count = this.count - 1) }
    }

    fun setName(name: String) = viewModelScope.launch {
        setState { copy(name = name) }
    }

    fun saveEntity() = viewModelScope.launch {
        setState { copy(isSaving = true, isSaved = false) }
        withState { state ->
            val entity = CountingEntity.Impl(name = state.name, counter = state.count, colour = getRandomColour())
            entityRepository.saveNewEntity(entity)
            setState { copy(isSaved = true, isSaving = false) }
        }
    }
}