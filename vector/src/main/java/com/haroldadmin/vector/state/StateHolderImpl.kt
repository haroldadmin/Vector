package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState
import com.haroldadmin.vector.loggers.logv
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * The default implementation of [StateHolder]
 *
 * @param initialState The initial state value to put in the [stateObservable]
 * @param logger A logger which can be used to record debug logs
 */
internal class StateHolderImpl<S : VectorState>(
    initialState: S,
    private val logger: Logger
) : StateHolder<S> {

    private val _stateObservable = MutableStateFlow(initialState)

    override val stateObservable: StateFlow<S>
        get() = _stateObservable

    override fun updateState(state: S) {
        _stateObservable.value = state
    }

    override fun clearHolder() {
        logger.logv { "Clearing State Holder" }
        // StateFlow does not need to be closed
    }
}