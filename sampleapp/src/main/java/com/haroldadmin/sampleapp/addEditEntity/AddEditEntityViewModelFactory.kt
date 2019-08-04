package com.haroldadmin.sampleapp.addEditEntity

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.haroldadmin.sampleapp.repository.EntitiesRepository

class AddEditEntityViewModelFactory(
    savedStateRegistryOwner: SavedStateRegistryOwner,
    defaultArgs: Bundle? = null,
    val entityId: String?,
    val repository: EntitiesRepository
) : AbstractSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ): T {
        if (modelClass == AddEditEntityViewModel::class.java) {
            return AddEditEntityViewModel(handle = handle, entityId = entityId, entityRepository =  repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel $modelClass requested")
        }
    }
}
