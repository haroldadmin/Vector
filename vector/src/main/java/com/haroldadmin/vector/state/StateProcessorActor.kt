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

/**
 * The default implementation of [StateProcessor] based on Actors in the Kotlin Coroutines library.
 *
 * @param S The state type associated with this processor
 * @param stateHolder The state holder where this processor can store and read state values
 * @param logger A [Logger] which can be used to log debug statements
 * @param coroutineContext The context of execution of this state processor
 */
internal class StateProcessorActor<S : VectorState>(
    private val stateHolder: StateHolder<S>,
    private val logger: Logger,
    override val coroutineContext: CoroutineContext
) : StateProcessor<S> {

    /**
     * Convenient way to read current state values from the [stateHolder]
     */
    private val currentState: S
        get() = stateHolder.state

    /**
     * Convenient way to access the stateObservable in [stateHolder]
     */
    private val stateChannel: ConflatedBroadcastChannel<S>
        get() = stateHolder.stateObservable

    /**
     * An actor which simply accepts incoming actions, adds them to the appropriate queue and then
     * flushes the queues one action at a time.
     */
    private val routingActor = actor<Action<S>>(
        context = coroutineContext,
        capacity = Channel.UNLIMITED
    ) {

        val setStateQueue: Queue<SetStateAction<S>> = ArrayDeque()
        val getStateQueue: Queue<GetStateAction<S>> = ArrayDeque()

        consumeEach { actionToBeRouted ->
            when (actionToBeRouted) {
                is SetStateAction -> {
                    logger.logv { "Enqueueing Set-State action" }
                    setStateQueue.offer(actionToBeRouted)
                }

                is GetStateAction -> {
                    logger.logv { "Enqueueing Get-State action" }
                    getStateQueue.offer(actionToBeRouted)
                }
            }

            while (channel.isEmpty && (setStateQueue.isNotEmpty() || getStateQueue.isNotEmpty())) {
                /**
                 * This algorithm may lead to the getStateBlocks never being run if a producer
                 * is producing set-state blocks fast enough. Consider investigating a priority based
                 * scheduling algorithm where priority of waiting actions is increased everytime
                 * an action is processed.
                 */
                val actionToBeProcessed = setStateQueue.poll() ?: getStateQueue.poll()
                ?: throw IllegalStateException("Queues are empty but there's no action to be processed")
                logger.logv { "Sending element to be processed" }
                stateProcessingActor.offer(actionToBeProcessed)
            }
        }
    }

    /**
     * An actor which retrieves the next [Action] to be processed from the queues,
     * and then processes it
     */
    val stateProcessingActor = actor<Action<S>>(
        context = coroutineContext,
        capacity = Channel.UNLIMITED
    ) {

        consumeEach { sentAction ->

            when (sentAction) {
                is SetStateAction -> {
                    logger.logv { "Processing Set-State action" }
                    val newState = sentAction.reducer.invoke(currentState)
                    stateChannel.offer(newState)
                }

                is GetStateAction -> {
                    logger.logv { "Processing Get-State action" }
                    sentAction.block.invoke(currentState)
                }
            }
        }
    }

    override fun offerSetAction(reducer: suspend S.() -> S) {
        routingActor.offer(SetStateAction(reducer))
    }

    override fun offerGetAction(action: suspend (S) -> Unit) {
        routingActor.offer(GetStateAction(action))
    }

    override fun clearProcessor() {
        logger.logv { "Clearing State Processor" }
        stateProcessingActor.close()
        routingActor.close()
        this.cancel()
    }
}