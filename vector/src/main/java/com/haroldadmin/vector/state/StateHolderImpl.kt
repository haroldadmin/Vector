package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

class StateHolderImpl<S : VectorState>(
    initialState: S,
    private val logger: Logger
) : StateHolder<S> {

    override val stateObservable = ConflatedBroadcastChannel(initialState)

    override fun clearHolder() {
        logger.log("Clearing State Holder")
        stateObservable.close()
    }
}