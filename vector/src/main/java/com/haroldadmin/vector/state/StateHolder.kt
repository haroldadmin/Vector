package com.haroldadmin.vector.state

import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

/**
 * Holds the current state value and provides access to it. A [ConflatedBroadcastChannel] is used
 * to hold the current state value. [clear] should be called when this state holder is no longer
 * in use.
 *
 * @param S The state type implementing [VectorState]
 */
interface StateHolder<S : VectorState> {

    /**
     * A [ConflatedBroadcastChannel] to expose the state as an observable entity.
     * This channel is conflated, so only the latest state value is present in it
     */
    val stateObservable: ConflatedBroadcastChannel<S>

    /**
     * A convenient way to access the current state value in the [stateObservable]
     */
    val state: S
        get() = stateObservable.valueOrNull
            ?: throw IllegalStateException("StateHolder was not created with a state value. Are you sure you set an initial state later before accessing the current state?")

    /**
     * This method is expected to be called when this state holder is no longer being used
     */
    fun clearHolder()
}