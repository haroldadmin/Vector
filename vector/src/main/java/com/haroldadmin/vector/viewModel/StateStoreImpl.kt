package com.haroldadmin.vector.viewModel

import com.haroldadmin.vector.Vector
import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import java.util.ArrayDeque
import kotlin.coroutines.CoroutineContext

internal interface Action<S : VectorState>
internal inline class SetStateAction<S : VectorState>(val reducer: suspend S.() -> S) : Action<S>
internal inline class GetStateAction<S : VectorState>(val block: suspend (S) -> Unit) : Action<S>

/**
 * An Implementation of [StateStore] interface. This class is expected to be owned by a
 * [VectorViewModel] which calls [cleanup] when it is cleared
 *
 * @param initialState The initial state object with which the owning ViewModel was created
 */
internal class StateStoreImpl<S : VectorState>(
    initialState: S,
    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job()
) : StateStore<S>, CoroutineScope {

    /**
     * A [ConflatedBroadcastChannel] to expose the latest value of state to its
     * subscribers
     */
    override val stateChannel = ConflatedBroadcastChannel(initialState)

    /**
     * A convenience property to access the current value of state without using the state channel
     */
    override val state: S
        get() = stateChannel.value

    /**
     * An actor that processes each incoming [Action].
     *
     * All [SetStateAction] messages are processed immediately
     * All [GetStateAction] messages are enqueued and processed when the channel is empty
     *
     * This means that before any GetStateAction can be processed, the channel must be free of any
     * SetStateActions. Therefore, the block inside the GetStateAction is always guaranteed to receive
     * the latest state.
     */
    private val actionsActor = actor<Action<S>>(capacity = Channel.UNLIMITED) {

        val getStateQueue = ArrayDeque<GetStateAction<S>>()

        consumeEach { action ->
            when (action) {
                is SetStateAction -> {
                    Vector.log("Processing set-state block")
                    val newState = action.reducer(state)
                    Vector.log("Sending new state to channel: $newState")
                    stateChannel.offer(newState)
                }
                is GetStateAction -> {
                    Vector.log("Processing get-state block")
                    getStateQueue.offer(action)
                }
            }

            getStateQueue
                .takeWhile { channel.isEmpty }
                .map { getStateAction ->
                    getStateAction.block(state)
                    getStateQueue.remove()
                }
        }
    }

    /**
     * Send a [SetStateAction] to [actionsActor] to be processed immediately if the channel is empty,
     * or after all preceeding [SetStateAction] objects in it.
     *
     * @param reducer The reducer to produce a new state from the given state.
     */
    override fun set(reducer: suspend S.() -> S) {
        actionsActor.offer(SetStateAction(reducer))
    }

    /**
     * Send a [GetStateAction] to [actionsActor] to be processed after all preceeding [SetStateAction]
     * objects. This ensures that the state parameter passed to [block] is always the latest.
     *
     * @param block The action to be executed using the given state
     */
    override fun get(block: suspend (S) -> Unit) {
        actionsActor.offer(GetStateAction(block))
    }

    /**
     * Cleanup all resources of this state store.
     */
    override fun cleanup() {
        actionsActor.close()
        stateChannel.close()
        this.cancel() // Cancel coroutine scope
    }
}
