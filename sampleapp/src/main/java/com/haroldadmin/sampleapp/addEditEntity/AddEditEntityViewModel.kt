package com.haroldadmin.sampleapp.addEditEntity

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.haroldadmin.sampleapp.CountingEntity
import com.haroldadmin.sampleapp.repository.EntitiesRepository
import com.haroldadmin.sampleapp.repository.getRandomColour
import com.haroldadmin.vector.FragmentViewModelOwner
import com.haroldadmin.vector.SavedStateVectorViewModel
import com.haroldadmin.vector.VectorViewModelFactory
import com.haroldadmin.vector.ViewModelOwner
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

class AddEditEntityViewModel @AssistedInject constructor(
    @Assisted initState: AddEditEntityState? = null,
    @Assisted handle: SavedStateHandle,
    private val entityRepository: EntitiesRepository
) : SavedStateVectorViewModel<AddEditEntityState>(
    initialState = initState,
    savedStateHandle = handle
) {

    init {
        if (initState is AddEditEntityState.EditEntity) {
            viewModelScope.launch {
                val entity = entityRepository.getEntity(initState.id)
                setState {
                    this as AddEditEntityState.EditEntity
                    copy(name = entity.name, count = entity.counter)
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
        savedStateHandle.set(KEY_SAVED_STATE, state)
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(initState: AddEditEntityState, handle: SavedStateHandle): AddEditEntityViewModel
    }

    companion object : VectorViewModelFactory<AddEditEntityViewModel, AddEditEntityState> {

        override fun initialState(
            handle: SavedStateHandle,
            owner: ViewModelOwner
        ): AddEditEntityState? {
            val persistedState: AddEditEntityState? = handle[KEY_SAVED_STATE]
            if (persistedState != null) return persistedState

            owner as FragmentViewModelOwner
            val entityId = owner.args()?.getString("entityId")

            return if (entityId == null) {
                AddEditEntityState.AddEntity()
            } else {
                AddEditEntityState.EditEntity(id = entityId)
            }
        }

        override fun create(
            initialState: AddEditEntityState,
            owner: ViewModelOwner,
            handle: SavedStateHandle
        ): AddEditEntityViewModel? {
            owner as FragmentViewModelOwner
            return owner.fragment<AddEditEntityFragment>().viewModelFactory.create(
                initialState,
                handle
            )
        }
    }
}