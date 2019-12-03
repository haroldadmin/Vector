package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState
import com.haroldadmin.vector.loggers.logv
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import java.util.ArrayDeque
import java.util.Queue
import kotlin.coroutines.CoroutineContext

internal class Benchmark_StateProcessorActor<S : VectorState>(
    private val stateHolder: StateHolder<S>,
    private val logger: Logger,
    override val coroutineContext: CoroutineContext
) : StateProcessor<S> {

    private val currentState: S
        get() = stateHolder.state

    private val stateChannel: ConflatedBroadcastChannel<S>
        get() = stateHolder.stateObservable

    private val routingActor = actor<Benchmark_Action<S>>(
        context = coroutineContext,
        capacity = Channel.UNLIMITED
    ) {

        val setStateQueue: Queue<Benchmark_SetStateAction<S>> = ArrayDeque()
        val getStateQueue: Queue<Benchmark_GetStateAction<S>> = ArrayDeque()

        consumeEach { actionToBeRouted ->
            when (actionToBeRouted) {
                is Benchmark_SetStateAction -> {
                    logger.logv { "Enqueueing Set-State action" }
                    setStateQueue.offer(actionToBeRouted)
                }

                is Benchmark_GetStateAction -> {
                    logger.logv { "Enqueueing Get-State action" }
                    getStateQueue.offer(actionToBeRouted)
                }
            }

            while (channel.isEmpty && (setStateQueue.isNotEmpty() || getStateQueue.isNotEmpty())) {
                val actionToBeProcessed = setStateQueue.poll() ?: getStateQueue.poll()
                ?: throw IllegalStateException("Queues are empty but there's no action to be processed")
                logger.logv { "Sending element to be processed" }
                stateProcessingActor.offer(actionToBeProcessed)
            }
        }
    }

    val stateProcessingActor = actor<Benchmark_Action<S>>(
        context = coroutineContext,
        capacity = Channel.UNLIMITED
    ) {

        consumeEach { sentAction ->

            when (sentAction) {
                is Benchmark_SetStateAction -> {
                    logger.logv { "Processing Set-State action" }
                    val newState = sentAction.reducer.invoke(currentState)
                    stateChannel.offer(newState)
                }

                is Benchmark_GetStateAction -> {
                    logger.logv { "Processing Get-State action" }
                    sentAction.block.invoke(currentState)
                }
            }
        }
    }

    override fun offerSetAction(reducer: suspend S.() -> S) {
        routingActor.offer(Benchmark_SetStateAction(reducer))
    }

    override fun offerGetAction(action: suspend (S) -> Unit) {
        routingActor.offer(Benchmark_GetStateAction(action))
    }

    override fun clearProcessor() {
        logger.logv { "Clearing State Processor" }
        stateProcessingActor.close()
        routingActor.close()
        this.cancel()
    }
}