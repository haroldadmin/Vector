package com.haroldadmin.vector

import androidx.lifecycle.SavedStateHandle

interface VectorViewModelFactory<VM : VectorViewModel<S>, S : VectorState> {

    fun initialState(handle: SavedStateHandle, owner: ViewModelOwner): S? { return null }

    fun create(initialState: S, owner: ViewModelOwner, handle: SavedStateHandle): VM? { return null }
}