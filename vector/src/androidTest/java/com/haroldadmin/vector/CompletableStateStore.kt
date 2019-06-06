package com.haroldadmin.vector

import com.haroldadmin.vector.viewModel.Action
import com.haroldadmin.vector.viewModel.SetStateAction
import com.haroldadmin.vector.viewModel.StateStore
import com.haroldadmin.vector.viewModel.StateStoreImpl
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import java.util.ArrayDeque
import kotlin.coroutines.CoroutineContext

private data class CompletableGetStateAction<S : VectorState>(
    val block: suspend (S) -> Unit,
    val status: CompletableDeferred<Unit> = CompletableDeferred()
) : Action<S>

internal class CompletableStateStore<S : VectorState>(
    initialState: S,
    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job(),
    stateStore: StateStore<S> = StateStoreImpl(initialState, coroutineContext)
) : StateStore<S> by stateStore, CoroutineScope {

    val stateStoreScope = CoroutineScope(coroutineContext)

    val actor = stateStoreScope.actor<Action<S>> {
        val getStateQueue = ArrayDeque<CompletableGetStateAction<S>>()

        consumeEach { action ->
            when (action) {
                is SetStateAction<S> -> {
                    val newState = action.reducer(state)
                    stateChannel.offer(newState)
                }

                is CompletableGetStateAction<S> -> {
                    getStateQueue.offer(action)
                }
            }

            getStateQueue
                .takeWhile { channel.isEmpty }
                .map { action ->
                    action.block(state)
                    action.status.complete(Unit)
                }
        }
    }

    fun completableGet(block: suspend (S) -> Unit): CompletableDeferred<Unit> {
        val action = CompletableGetStateAction(block)
        actor.offer(action)
        return action.status
    }
}