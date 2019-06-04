package com.haroldadmin.vector.viewModel

import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

/**
 * Manages synchronized accesses and mutations to state, so that functions trying to access state
 * always receive the latest state, and functions trying to set state execute only after all previous
 * state mutations have been processed.
 *
 * This class is expected to be owned by a [VectorViewModel] which calls [cleanup] when it is cleared.
 *
 * @param S The subclass of [VectorState] on which this class is based. For convenience, use a Kotlin data class
 */
interface StateStore <S : VectorState> {

    /**
     * A convenient way to access the current state value in the [stateChannel]
     */
    val state: S

    /**
     * Takes in a state reducer, adds it to a queue.
     * This is used to mutate state by sending in a reducer
     *
     * Example:
     * stateStore.set { copy(count = counter + 1) }
     */
    suspend fun set(action: suspend S.() -> S)

    /**
     * Takes in a block that needs to access state and perform some action with it
     *
     * The supplied state parameter is guaranteed to always be the latest state,
     * even if there are other state mutation blocks in the queue
     */
    suspend fun get(block: suspend (S) -> Unit)

    /**
     * A [ConflatedBroadcastChannel] to expose the state as an observable entity.
     * Any new state produced by the reducers given to [set] is passed to this channel.
     *
     * This channel is conflated, so only the latest state value is present in it
     */
    val stateChannel: ConflatedBroadcastChannel<S>

    /**
     * This method is expected to be called by the owning ViewModel of this class
     * to cleanup the resources, and close all channels in this State Store
     */
    fun cleanup()
}