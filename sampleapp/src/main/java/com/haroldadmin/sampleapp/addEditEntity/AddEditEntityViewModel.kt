package com.haroldadmin.sampleapp.addEditEntity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.haroldadmin.sampleapp.CountingEntity
import com.haroldadmin.sampleapp.repository.EntitiesRepository
import com.haroldadmin.sampleapp.repository.getRandomColour
import com.haroldadmin.vector.SavedStateVectorViewModel
import kotlinx.coroutines.launch

private const val KEY_STATE = "state"

class AddEditEntityViewModel(
    initState: AddEditEntityState? = null,
    handle: SavedStateHandle,
    private val entityId: String?,
    private val entityRepository: EntitiesRepository
) : SavedStateVectorViewModel<AddEditEntityState>(
    initialState = initState,
    savedStateHandle = handle
) {

    init {
        viewModelScope.launch {

            val persistedState: AddEditEntityState? = savedStateHandle[KEY_STATE]

            when {
                persistedState != null -> {
                    setInitialState(persistedState)
                }

                entityId != null -> {
                    val entity = entityRepository.getEntity(entityId)
                    setInitialState(
                        AddEditEntityState.EditEntity(
                            id = entity.id,
                            name = entity.name,
                            count = entity.counter
                        )
                    )
                }
                else -> {
                    setInitialState(
                        AddEditEntityState.AddEntity()
                    )
                }
            }
        }
    }

    fun incrementCount() = viewModelScope.launch {
        withState { state ->
            when (state) {
                is AddEditEntityState.AddEntity -> setState {
                    this as AddEditEntityState.AddEntity
                    copy(count = this.count + 1, isSaved = false)
                }
                is AddEditEntityState.EditEntity -> setState {
                    this as AddEditEntityState.EditEntity
                    copy(count = this.count + 1, isSaved = false)
                }
            }

            persistState()
        }
    }

    fun decrementCount() = viewModelScope.launch {
        withState { state ->
            when (state) {
                is AddEditEntityState.AddEntity -> setState {
                    this as AddEditEntityState.AddEntity
                    copy(count = this.count - 1, isSaved = false)
                }
                is AddEditEntityState.EditEntity -> setState {
                    this as AddEditEntityState.EditEntity
                    copy(count = this.count - 1, isSaved = false)
                }
            }
            savedStateHandle.set(KEY_STATE, state)

            persistState()
        }
    }

    fun setName(name: String) = viewModelScope.launch {
        withState { state ->
            when (state) {
                is AddEditEntityState.AddEntity -> setState {
                    this as AddEditEntityState.AddEntity
                    copy(name = name, isSaved = false)
                }
                is AddEditEntityState.EditEntity -> setState {
                    this as AddEditEntityState.EditEntity
                    copy(name = name, isSaved = false)
                }
            }
        }
        persistState()
    }

    fun saveEntity() = viewModelScope.launch {
        withState { state ->
            when (state) {
                is AddEditEntityState.AddEntity -> setState {
                    this as AddEditEntityState.AddEntity
                    val entity = CountingEntity.Impl(
                        id = this.id,
                        name = this.name,
                        counter = this.count,
                        colour = getRandomColour()
                    )
                    entityRepository.saveNewEntity(entity)
                    copy(isSaved = true)
                }

                is AddEditEntityState.EditEntity -> setState {
                    this as AddEditEntityState.EditEntity
                    val entity = CountingEntity.Impl(
                        id = this.id,
                        name = this.name,
                        counter = this.count,
                        colour = getRandomColour()
                    )
                    entityRepository.updateEntity(entity)
                    copy(isSaved = true)
                }
            }

            persistState()
        }
    }

    private fun persistState() = withState { state ->
        savedStateHandle.set(KEY_STATE, state)
    }
}