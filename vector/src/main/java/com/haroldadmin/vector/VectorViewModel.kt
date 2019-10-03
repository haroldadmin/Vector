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
 * @param logger The [Logger] to use for debug logs
 *
 * A [VectorViewModel] can implement the [VectorViewModelFactory] in its Companion object
 * to provide ways to create the initial state, as well as the ViewModel itself.
 */
abstract class VectorViewModel<S : VectorState>(
    initialState: S,
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job(),
    protected val logger: Logger = androidLogger()
) : ViewModel() {

    /**
     * The state store associated with this ViewModel
     */
    protected open val stateStore = StateStoreFactory.create(initialState, logger, stateStoreContext)

    /**
     * A [kotlinx.coroutines.flow.Flow] of [VectorState] which can be observed by external classes to respond to changes in state.
     */
    val state: Flow<S> by lazy {
        stateStore
            .stateObservable
            .asFlow()
    }

    /**
     * Access the current value of state stored in the [stateStore].
     *
     * **THIS VALUE OF STATE IS NOT GUARANTEED TO BE UP TO DATE**
     * This property is only meant to be used by external classes who need to get hold of the current state
     * without having to subscribe to it. For use cases where the current state is needed to be accessed inside the
     * ViewModel, the [withState] method should be used
     */
    val currentState: S
        get() = stateStore.state

    /**
     * The only method through which state mutation is allowed in subclasses.
     *
     * Dispatches an action the [stateStore]. This action shall be processed as soon as possible in
     * the state store, but not necessarily immediately
     *
     * @param action The state reducer to create a new state from the current state
     *
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
     *
     */
    protected fun withState(action: suspend (S) -> Unit) {
        stateStore.offerGetAction(action)
    }

    /**
     * Clears this ViewModel as well as its [stateStore].
     */
    @CallSuper
    override fun onCleared() {
        logger.log("Clearing ViewModel")
        super.onCleared()
        stateStore.clear()
    }
}