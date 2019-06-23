package com.haroldadmin.sampleapp.addEntity

import androidx.lifecycle.viewModelScope
import com.haroldadmin.sampleapp.CountingEntity
import com.haroldadmin.sampleapp.repository.Colour
import com.haroldadmin.sampleapp.repository.EntitiesRepository
import com.haroldadmin.vector.viewModel.VectorViewModel
import kotlinx.coroutines.launch

class AddEntityViewModel(
    initState: AddEntityState?,
    private val entityRepository: EntitiesRepository
) : VectorViewModel<AddEntityState>(initState ?: AddEntityState()) {

    fun incrementCount() = viewModelScope.launch {
        setState { copy(temporaryEntity = TemporaryEntity(count = currentState.temporaryEntity.count + 1)) }
    }

    fun decrementCount() = viewModelScope.launch {
        setState { copy(temporaryEntity = TemporaryEntity(count = currentState.temporaryEntity.count - 1)) }
    }

    fun saveEntity(name: String) = viewModelScope.launch {
        setState { copy(isSaving = true, isSaved = false) }
        withState { state ->
            val entity = state.temporaryEntity.copy(name = name).toEntity()
            entityRepository.saveNewEntity(entity)
            setState { copy(isSaved = true, isSaving = false) }
        }
    }

    private fun TemporaryEntity.toEntity(): CountingEntity {
        return CountingEntity.Impl(name = this.name, counter = this.count, colour = Colour.RED)
    }
}