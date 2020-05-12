package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState
import com.haroldadmin.vector.loggers.logv
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * The default implementation of [StateHolder]
 *
 * @param initialState The initial state value to put in the [stateObservable]
 * @param logger A logger which can be used to record debug logs
 *
 */
internal class StateHolderImpl<S : VectorState>(
    initialState: S,
    private val logger: Logger
) : StateHolder<S> {

    override val stateObservable: MutableStateFlow<S> = MutableStateFlow(initialState)

    override fun updateState(state: S) {
        stateObservable.value = state
    }

    override fun clearHolder() {
        logger.logv { "Clearing State Holder" }
    }
}