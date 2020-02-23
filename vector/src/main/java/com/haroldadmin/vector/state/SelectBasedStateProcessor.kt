package com.haroldadmin.vector.state

import com.haroldadmin.vector.VectorState
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.logd
import com.haroldadmin.vector.loggers.logv
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.coroutines.CoroutineContext

/**
 * A [StateProcessor] which processes jobs sent to it sequentially, prioritizing state reducers over actions.
 *
 * This implementation is based on the [select] statement rather than an [kotlinx.coroutines.channels.actor].
 * Additionally, it supports startup under a lazy mode to facilitate testing. If it is created under lazy mode, it
 * does not begin processing jobs sent to it until the [start] method is called.
 *
 * Benchmarks suggest that this implementation is about 50% faster than the Actors based implementation.
 *
 * @param isLazy if true, jobs sent to this processor begin processing only after [start] is called, or immediately after creation otherwise
 * @param stateHolder the [StateHolder] where this processor can store and read the current state
 * @param logger a [Logger] to log miscellaneous information
 * @param coroutineContext The [CoroutineContext] under which this processor will execute jobs sent to it
 */
internal class SelectBasedStateProcessor<S : VectorState>(
    isLazy: Boolean = false,
    private val stateHolder: StateHolder<S>,
    private val logger: Logger,
    override val coroutineContext: CoroutineContext
) : StateProcessor<S> {

    /**
     * Queue for state reducers.
     * Has unlimited capacity so that sending new elements to it is not a blocking operation
     **/
    private val setStateChannel: Channel<reducer<S>> = Channel(Channel.UNLIMITED)

    /**
     * Queue for actions on the current state.
     * Has unlimited capacity so that sending new elements to it is not a blocking operation
     **/
    private val getStateChannel: Channel<action<S>> = Channel(Channel.UNLIMITED)

    init {
        if (isLazy) {
            logger.logv { "Starting in Lazy mode. Call start() to begin processing actions and reducers" }
        } else {
            start()
        }
    }

    /**
     * Enqueues the given [reducer] to an internal queue
     *
     * If the state processor has been cleared before this reducer is offered, then it is ignored and not added
     * to the queue to be processed
     */
    override fun offerSetAction(reducer: suspend S.() -> S) {
        if (!setStateChannel.isClosedForSend) {
            // TODO Look for a solution to the case where the channel could be closed between the check and this offer
            //  statement
            setStateChannel.offer(reducer)
        }
    }

    /**
     * Enqueues the given [action] to an internal queue
     *
     * If the state processor has been cleared before this action is offered, then it is ignored and not added
     * to the queue to be processed.
     */
    override fun offerGetAction(action: suspend (S) -> Unit) {
        if (!getStateChannel.isClosedForSend) {
            // TODO Look for a solution to the case where the channel could be closed between the check and this offer
            //  statement
            getStateChannel.offer(action)
        }
    }

    /**
     * Cancels this processor's coroutine scope and stops processing of jobs.
     *
     * Repeated invocations have no effect.
     */
    override fun clearProcessor() {
        if (isActive && !setStateChannel.isClosedForSend && !getStateChannel.isClosedForSend) {
            logger.logd { "Clearing StateProcessor $this" }
        }
        this.cancel()
        setStateChannel.close()
        getStateChannel.close()
    }

    /**
     * Launches a coroutine to start processing jobs sent to it.
     */
    internal fun start() = launch {
        while (isActive) {
            select<Unit> {
                setStateChannel.onReceive { reducer ->
                    stateHolder.stateObservable.valueOrNull?.let { state ->
                        val newState = state.reducer()
                        if (!stateHolder.stateObservable.isClosedForSend) {
                            stateHolder.stateObservable.offer(newState)
                        }
                    }
                }
                getStateChannel.onReceive { action ->
                    launch {
                        stateHolder.stateObservable.valueOrNull?.let { state ->
                            action.invoke(stateHolder.state)
                        }
                    }
                }
            }
        }
    }
}

private typealias reducer<S> = suspend S.() -> S

private typealias action<S> = suspend (S) -> Unit