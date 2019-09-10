package com.haroldadmin.vector

import androidx.annotation.CallSuper
import androidx.lifecycle.ViewModel
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.androidLogger
import com.haroldadmin.vector.state.StateStoreFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlin.coroutines.CoroutineContext

/**
 * The Base ViewModel class your ViewModel should inherit from
 *
 * @param S The state class for this ViewModel implementing [VectorState]
 * @param initialState The initial state for this ViewModel
 * @param stateStoreContext The [CoroutineContext] to be used with the state store
 *
 * A [VectorViewModel] can implement the [VectorViewModelFactory] in its Companion object
 * to provide ways to create the initial state, as well as the ViewModel itself.
 */
abstract class VectorViewModel<S : VectorState>(
    initialState: S?,
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job(),
    protected val logger: Logger = androidLogger()
) : ViewModel() {

    protected open val stateStore = if (initialState != null) {
        StateStoreFactory.create(initialState, logger, stateStoreContext)
    } else {
        StateStoreFactory.create(logger, stateStoreContext)
    }

    val state: Flow<S> by lazy {
        stateStore
            .stateObservable
            .asFlow()
            .filterNotNull()
    }

    val currentState: S
        get() = stateStore.state

    /**
     * Allows setting an initial state after the ViewModel has been constructed.
     *
     * This is useful when the ViewModel was created without an initial state.
     * [setState] function should **NOT** to set this state.
     */
    @Deprecated(
        """
        This method should not be used anymore. Your ViewModel should implement a VectorViewModelFactory
        in its companion object, and override the initialState() method to get the initial state.
        
        THIS METHOD WILL BE REMOVED BEFORE 1.0 release.
        """
    )
    protected fun setInitialState(state: S) {
        stateStore.setInitialState(state)
    }

    /**
     * The only method through which state mutation is allowed in subclasses.
     *
     * Dispatches an action the [stateStore]. This action shall be processed as soon as possible in
     * the state store, but not necessarily immediately
     *
     * @param action The state reducer to create a new state from the current state
     */
    protected fun setState(action: suspend S.() -> S) {
        stateStore.offerSetAction(action)
    }

    /**
     * Dispatch the given action the [stateStore]. This action shall be processed as soon as all existing
     * state reducers have been processed. The state parameter supplied to this action should be the
     * latest value at the time of processing of this action
     *
     * @param action The action to be performed with the current state
     */
    protected fun withState(action: suspend (S) -> Unit) {
        stateStore.offerGetAction(action)
    }

    @CallSuper
    override fun onCleared() {
        logger.log("Clearing ViewModel")
        super.onCleared()
        stateStore.clear()
    }
}