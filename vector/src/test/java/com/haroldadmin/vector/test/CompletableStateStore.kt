package com.haroldadmin.vector.test

import com.haroldadmin.vector.VectorState
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

internal data class CompletableGetStateAction<S : VectorState>(
    val block: suspend (S) -> Unit,
    val status: CompletableDeferred<Boolean> = CompletableDeferred()
) : Action<S>

internal class CompletableStateStore<S : VectorState>(
    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job(),
    initialState: S,
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
                    action.status.complete(true)
                }
        }

    }

    fun completableGet(block: suspend (S) -> Unit): CompletableDeferred<Boolean> {
        val action = CompletableGetStateAction(block)
        actor.offer(action)
        return action.status
    }
}