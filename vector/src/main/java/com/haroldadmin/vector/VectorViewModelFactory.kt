package com.haroldadmin.vector

import androidx.lifecycle.SavedStateHandle

interface VectorViewModelFactory<VM: VectorViewModel<S>, S: VectorState> {

    fun initialState(handle: SavedStateHandle): S? { return null }

    fun create(initialState: S): VM? { return null }

}