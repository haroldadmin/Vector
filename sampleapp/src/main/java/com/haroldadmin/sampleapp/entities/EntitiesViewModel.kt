package com.haroldadmin.sampleapp.entities

import androidx.lifecycle.viewModelScope
import com.haroldadmin.sampleapp.repository.EntitiesRepository
import com.haroldadmin.vector.viewModel.VectorViewModel
import kotlinx.coroutines.launch

class EntitiesViewModel(
    initState: EntitiesState?,
    private val repository: EntitiesRepository
) : VectorViewModel<EntitiesState>(initState ?: EntitiesState(entities = null)) {

    fun getAllEntities() = viewModelScope.launch {
        val entities = repository.getAllEntities()
        setState {
            val newState = copy(entities = entities, isLoading = false)
            newState
        }
    }
}
