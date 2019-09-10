package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

/**
 * The default implementation of [StateHolder]
 *
 * @param initialState The initial state value to put in the [stateObservable]
 *
 * **IMPORTANT**
 * If this value is not supplied, or is supplied as null, then it is the responsibility of the owning
 * [com.haroldadmin.vector.VectorViewModel] or [StateStore] to supply an initial state as soon as possible
 *
 * @param logger A logger which can be used to record debug logs
 */
internal class StateHolderImpl<S : VectorState>(
    initialState: S?,
    private val logger: Logger
) : StateHolder<S> {

    override val stateObservable = initialState?.let {
        ConflatedBroadcastChannel(it)
    } ?: ConflatedBroadcastChannel()

    override fun clearHolder() {
        logger.log("Clearing State Holder")
        stateObservable.close()
    }
}