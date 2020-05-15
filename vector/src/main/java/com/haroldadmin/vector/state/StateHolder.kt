package com.haroldadmin.vector.state

import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.StateFlow

/**
 * Holds the current state value and provides access to it. A [ConflatedBroadcastChannel] is used
 * to hold the current state value. [clear] should be called when this state holder is no longer
 * in use.
 *
 * @param S The state type implementing [VectorState]
 */
interface StateHolder<S : VectorState> {

    /**
     * A [StateFlow] to expose the state as an observable entity.
     * This flow is conflated, so only the latest state value is present in it
     *
     * To be notified of every state update, use the [kotlinx.coroutines.flow.buffer] operator.
     */
    val stateObservable: StateFlow<S>

    /**
     * A convenient way to access the current state value in the [stateObservable]
     */
    val state: S
        get() = stateObservable.value

    /**
     * Updates the state contained in this state holder
     */
    fun updateState(newState: S)

    /**
     * This method is expected to be called when this state holder is no longer being used
     */
    fun clearHolder()
}