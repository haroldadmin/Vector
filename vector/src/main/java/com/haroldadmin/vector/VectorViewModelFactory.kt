package com.haroldadmin.vector

import androidx.lifecycle.SavedStateHandle

/**
 * A Factory meant to be implemented using the Companion object of a [VectorViewModel] to provide
 * ways to create the initial state, as well as the creation of the ViewModel itself.
 */
interface VectorViewModelFactory<VM : VectorViewModel<S>, S : VectorState> {

    /**
     * Used to create the initial state, if this method is implemented.
     *
     * @param handle Can be used to retrieve last saved state before process death,
     * using [SavedStateVectorViewModel.KEY_SAVED_STATE] or some other mechanism.
     * @param owner The [ViewModelOwner] for this ViewModel. Can be used to access fragment arguments,
     * context etc.
     *
     * @return Initial state to be used for creation of the ViewModel
     */
    fun initialState(handle: SavedStateHandle, owner: ViewModelOwner): S? { return null }

    /**
     * Used to create the ViewModel itself. This method needs to be implemented if your ViewModel
     * has dependencies other than those of a [VectorViewModel] or a [SavedStateVectorViewModel].
     *
     * However, if you are using a different kind of factory to create your ViewModel, you might skip implementing
     * this function.
     *
     * @param initialState The initial state to be given to the ViewModel
     * @param owner The [ViewModelOwner] for this ViewModel. Can be used to access context, dependency graph, etc.
     * @param handle The saved state handle to be given to the ViewModel, if needed.
     *
     * @return The ViewModel to be created using this function.
     */
    fun create(initialState: S, owner: ViewModelOwner, handle: SavedStateHandle): VM? { return null }
}