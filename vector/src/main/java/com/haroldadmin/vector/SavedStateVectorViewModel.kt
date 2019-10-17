package com.haroldadmin.vector

import androidx.lifecycle.SavedStateHandle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * A Subclass of [VectorViewModel] that has access to a [SavedStateHandle] to easily
 * persist state properties in case of process death
 *
 * @param initialState The initial state for this ViewModel
 * @param stateStoreContext The [CoroutineContext] to be used with the contained State Store
 * @param savedStateHandle The [SavedStateHandle] to be used for persisting state across process deaths
 */
abstract class SavedStateVectorViewModel<S : VectorState>(
    initialState: S,
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job(),
    protected val savedStateHandle: SavedStateHandle
) : VectorViewModel<S>(initialState, stateStoreContext) {

    companion object {
        /**
         * A predefined key which can be used to persist a valid [VectorState] class into the
         * [savedStateHandle]
         */
        const val KEY_SAVED_STATE = "vector:saved-state"
    }

    /**
     * A convenience wrapper around the [setState] function which runs the given reducer, and then
     * persists the newly created state
     *
     * @param reducer The state reducer to create a new state from the current state
     *
     */
    protected fun setStateAndPersist(reducer: suspend S.() -> S) {
        setState(reducer)
        persistState()
    }

    /**
     * Saves the current state into [savedStateHandle] using [KEY_SAVED_STATE]
     * Subclasses can override this method for custom behaviour.
     */
    protected open fun persistState() = withState { state ->
        logger.log("Persisting state: $state")
        savedStateHandle.set(KEY_SAVED_STATE, state)
    }
}