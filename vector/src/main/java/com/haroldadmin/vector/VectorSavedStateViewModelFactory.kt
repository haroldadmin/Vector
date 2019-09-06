package com.haroldadmin.vector

import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.savedstate.SavedStateRegistryOwner

class VectorSavedStateViewModelFactory<V>(
    owner: SavedStateRegistryOwner,
    defaultArgs: Bundle?,
    private val factory: (Class<*>, SavedStateHandle) -> V
) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(
        key: String,
        modelClass: Class<T>,
        handle: SavedStateHandle
    ) = factory(modelClass, handle) as T
}