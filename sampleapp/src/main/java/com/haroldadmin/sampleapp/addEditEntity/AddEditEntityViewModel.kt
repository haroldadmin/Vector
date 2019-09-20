package com.haroldadmin.sampleapp.addEditEntity

import android.os.Bundle
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
    @Assisted initState: AddEditEntityState,
    @Assisted handle: SavedStateHandle,
    private val entityRepository: EntitiesRepository
) : SavedStateVectorViewModel<AddEditEntityState>(
    initialState = initState,
    savedStateHandle = handle
) {

    fun incrementCount() = viewModelScope.launch {
        withState { state ->
            when (state) {
                is AddEditEntityState.AddEntity -> setStateAndPersist {
                    this as AddEditEntityState.AddEntity
                    copy(count = this.count + 1, isSaved = false)
                }
                is AddEditEntityState.EditEntity -> setStateAndPersist {
                    this as AddEditEntityState.EditEntity
                    copy(count = this.count + 1, isSaved = false)
                }
            }
        }
    }

    fun decrementCount() = viewModelScope.launch {
        withState { state ->
            when (state) {
                is AddEditEntityState.AddEntity -> setStateAndPersist {
                    this as AddEditEntityState.AddEntity
                    copy(count = this.count - 1, isSaved = false)
                }
                is AddEditEntityState.EditEntity -> setStateAndPersist {
                    this as AddEditEntityState.EditEntity
                    copy(count = this.count - 1, isSaved = false)
                }
            }
        }
    }

    fun setName(name: String) = viewModelScope.launch {
        withState { state ->
            when (state) {
                is AddEditEntityState.AddEntity -> setStateAndPersist {
                    this as AddEditEntityState.AddEntity
                    copy(name = name, isSaved = false)
                }
                is AddEditEntityState.EditEntity -> setStateAndPersist {
                    this as AddEditEntityState.EditEntity
                    copy(name = name, isSaved = false)
                }
            }
        }
    }

    fun saveEntity() = viewModelScope.launch {
        withState { state ->
            when (state) {
                is AddEditEntityState.AddEntity -> setStateAndPersist {
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

                is AddEditEntityState.EditEntity -> setStateAndPersist {
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
        }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(initState: AddEditEntityState, handle: SavedStateHandle): AddEditEntityViewModel
    }

    companion object : VectorViewModelFactory<AddEditEntityViewModel, AddEditEntityState> {

        override fun create(
            initialState: AddEditEntityState,
            owner: ViewModelOwner,
            handle: SavedStateHandle
        ): AddEditEntityViewModel {
            throw IllegalStateException("This ViewModel should be created using the AssistedInject Factory only")
        }

        override fun initialState(
            handle: SavedStateHandle,
            owner: ViewModelOwner
        ): AddEditEntityState? {

            val persistedState: AddEditEntityState? = handle[KEY_SAVED_STATE]

            persistedState?.let {
                return it
            } ?: run {

                val args = (owner as FragmentViewModelOwner).args()
                val (entityId, entityName, counter) = parseArgs(args!!)

                return if (entityId.isBlank()) {
                    AddEditEntityState.AddEntity()
                } else {
                    AddEditEntityState.EditEntity(entityId, entityName, counter)
                }
            }
        }

        private fun parseArgs(args: Bundle): Triple<String, String, Long> {
            val entityId = args.getString("entityId") ?: ""
            val entityName = args.getString("entityName") ?: ""
            val count = args.getLong("entityCount")

            return Triple(entityId, entityName, count)
        }
    }
}