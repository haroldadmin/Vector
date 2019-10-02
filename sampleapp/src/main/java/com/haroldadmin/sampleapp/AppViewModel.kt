package com.haroldadmin.sampleapp

import androidx.lifecycle.viewModelScope
import com.haroldadmin.sampleapp.repository.EntitiesRepository
import com.haroldadmin.vector.VectorState
import com.haroldadmin.vector.VectorViewModel
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.launch

data class AppState(val numberOfEntities: Long = 0) : VectorState

class AppViewModel @AssistedInject constructor(
    @Assisted initialState: AppState,
    private val repository: EntitiesRepository
) : VectorViewModel<AppState>(initialState) {

    fun updateNumberOfEntities() = viewModelScope.launch {
        println("${this@AppViewModel}: Updating number of entities")
        val numberOfEntities = repository.getNumberOfEntities()
        setState { copy(numberOfEntities = numberOfEntities) }
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(initialState: AppState): AppViewModel
    }
}