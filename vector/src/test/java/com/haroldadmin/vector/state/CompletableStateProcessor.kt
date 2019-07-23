package com.haroldadmin.vector.state

import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlin.coroutines.CoroutineContext

internal data class CompletableGetStateAction<S : VectorState>(
    val block: suspend (S) -> Unit,
    val status: CompletableDeferred<Unit> = CompletableDeferred()
) : Action<S>

internal data class CompletableSetStateAction<S : VectorState>(
    val reducer: suspend S.() -> S,
    val status: CompletableDeferred<Unit> = CompletableDeferred()
) : Action<S>

class CompletableStateProcessor<S: VectorState>(
    val stateHolder: StateHolder<S>,
    override val coroutineContext: CoroutineContext
): StateProcessor<S>, CoroutineScope {

    val stateChannel: ConflatedBroadcastChannel<S>
        get() = stateHolder.stateObservable

    val currentState: S
        get() = stateHolder.stateObservable.value

    private val setStateChannel = Channel<CompletableSetStateAction<S>>(capacity = Channel.UNLIMITED)
    private val getStateChannel = Channel<CompletableGetStateAction<S>>(capacity = Channel.UNLIMITED)

    private val stateProcessingActor = actor<Unit>(
        context = coroutineContext,
        capacity = Channel.UNLIMITED
    ) {
        consumeEach {
            when (val sentAction = setStateChannel.poll() ?: getStateChannel.poll()) {
                is CompletableSetStateAction -> {
                    val newState = sentAction.reducer(currentState)
                    sentAction.status.complete(Unit)
                    stateChannel.offer(newState)
                }

                is CompletableGetStateAction -> {
                    sentAction.block(currentState)
                    sentAction.status.complete(Unit)
                }

                else -> Unit
            }
        }
    }

    override fun offerSetAction(action: suspend S.() -> S) {
        throw UnsupportedOperationException("Use completable set state action instead")
    }

    fun completableSetAction(action: suspend S.() -> S): Deferred<Unit> {
        val completableAction = CompletableSetStateAction(action)
        setStateChannel.offer(completableAction)
        stateProcessingActor.offer(Unit)
        return completableAction.status
    }

    override fun offerGetAction(action: suspend (S) -> Unit) {
        throw UnsupportedOperationException("Use completable get state action instead")
    }

    fun completableGetAction(action: suspend (S) -> Unit): Deferred<Unit> {
        val completableAction = CompletableGetStateAction(action)
        getStateChannel.offer(completableAction)
        stateProcessingActor.offer(Unit)
        return completableAction.status
    }

    override fun clearProcessor() {
        setStateChannel.close()
        getStateChannel.close()
        stateProcessingActor.close()
        this.cancel()
    }

}