package com.haroldadmin.vector.state

import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

/**
 * Manages synchronized accesses and mutations to state, so that functions trying to access state
 * always receive the latest state, and functions trying to set state execute only after all previous
 * state mutations have been processed.
 *
 * This class is expected to be owned by a [VectorViewModel] which calls [clear] when it is cleared.
 *
 * @param S The subclass of [VectorState] on which this class is based. For convenience, use a Kotlin data class
 */
interface StateHolder <S : VectorState> {

    /**
     * A [ConflatedBroadcastChannel] to expose the state as an observable entity.
     * Any new state produced by the reducers given to [set] is passed to this channel.
     *
     * This channel is conflated, so only the latest state value is present in it
     */
    val stateObservable: ConflatedBroadcastChannel<S>

    /**
     * A convenient way to access the current state value in the [stateObservable]
     */
    val state: S
        get() = stateObservable.value

    /**
     * This method is expected to be called by the owning ViewModel of this class
     * to clear the resources, and close all channels in this State Store
     */
    fun clearHolder()
}