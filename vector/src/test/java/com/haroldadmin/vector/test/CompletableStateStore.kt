package com.haroldadmin.vector.test

import com.haroldadmin.vector.VectorState
import com.haroldadmin.vector.viewModel.Action
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
    val status: CompletableDeferred<Unit> = CompletableDeferred()
) : Action<S>

internal data class CompletableSetStateAction<S : VectorState>(
    val reducer: suspend S.() -> S,
    val status: CompletableDeferred<Unit> = CompletableDeferred()
) : Action<S>

internal class CompletableStateStore<S : VectorState>(
    override val coroutineContext: CoroutineContext = Dispatchers.Default + Job(),
    initialState: S,
    stateStore: StateStore<S> = StateStoreImpl(initialState, coroutineContext)
) : StateStore<S> by stateStore, CoroutineScope {

    val actor = actor<Action<S>> {
        val getStateQueue = ArrayDeque<CompletableGetStateAction<S>>()

        consumeEach { action ->
            when (action) {
                is CompletableSetStateAction<S> -> {
                    val newState = action.reducer(state)
                    action.status.complete(Unit)
                    stateChannel.offer(newState)
                }

                is CompletableGetStateAction<S> -> {
                    getStateQueue.offer(action)
                }
            }

            getStateQueue
                .takeWhile { channel.isEmpty }
                .map { getStateAction ->
                    getStateAction.block(state)
                    getStateAction.status.complete(Unit)
                }
        }
    }

    override fun get(block: suspend (S) -> Unit) {
        throw UnsupportedOperationException("Completable state store should only be used with completable get/set operations")
    }

    override fun set(action: suspend S.() -> S) {
        throw UnsupportedOperationException("Completable state store should only be used with completable get/set operations")
    }

    fun completableGet(block: suspend (S) -> Unit): CompletableDeferred<Unit> {
        val action = CompletableGetStateAction(block)
        actor.offer(action)
        return action.status
    }

    fun completableSet(block: suspend S.() -> S): CompletableDeferred<Unit> {
        val action = CompletableSetStateAction(block)
        actor.offer(action)
        return action.status
    }
}