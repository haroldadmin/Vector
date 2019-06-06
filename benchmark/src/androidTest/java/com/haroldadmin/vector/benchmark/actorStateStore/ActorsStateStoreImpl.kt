package com.haroldadmin.vector.benchmark.actorStateStore

import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import java.util.ArrayDeque
import kotlin.coroutines.CoroutineContext

private interface Action<S: VectorState>
private inline class SetStateAction<S: VectorState>(val reducer: suspend S.() -> S): Action<S>
private inline class GetStateAction<S: VectorState>(val block: suspend (S) -> Unit): Action<S>

/**
 * An Implementation of [StateStore] interface. This class is expected to be owned by a
 * [VectorViewModel] which calls [cleanup] when it is cleared
 *
 * @param initialState The initial state object with which the owning ViewModel was created
 */
internal class ActorsStateStoreImpl<S : VectorState>(
    initialState: S,
    override val coroutineContext: CoroutineContext = Dispatchers.Default
) : ActorsStateStore<S>, CoroutineScope {

    /**
     * A [ConflatedBroadcastChannel] to expose the latest value of state to its
     * subscribers
     */
    override val stateChannel = ConflatedBroadcastChannel(initialState)

    override val state: S
        get() = stateChannel.value


    private val actionsActor = actor<Action<S>>(capacity = Channel.UNLIMITED) {

            val getStateQueue = ArrayDeque<suspend (S) -> Unit>()

            consumeEach { action ->
                when (action) {
                    is SetStateAction -> {
                        val newState = action.reducer(state)
                        stateChannel.offer(newState)
                    }
                    is GetStateAction -> {
                        getStateQueue.offer(action.block)
                    }
                }

                getStateQueue
                    .takeWhile { channel.isEmpty }
                    .map { block -> block(state) }
            }
        }

    override fun set(action: suspend S.() -> S) {
        actionsActor.offer(SetStateAction(action))
    }

    override fun get(block: suspend (S) -> Unit) {
        actionsActor.offer(GetStateAction(block))
    }

    override fun cleanup() {
        actionsActor.close()
        stateChannel.close()
        this.cancel() // Cancel coroutine scope
    }
}
