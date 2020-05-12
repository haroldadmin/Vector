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

internal interface Benchmark_Action<S : VectorState>

internal inline class Benchmark_SetStateAction<S : VectorState>(val reducer: suspend S.() -> S) :
    Benchmark_Action<S>

internal inline class Benchmark_GetStateAction<S : VectorState>(val block: suspend (S) -> Unit) :
    Benchmark_Action<S>

internal class Benchmark_SelectBasedStateProcessor<S : VectorState>(
    isLazy: Boolean = false,
    private val stateHolder: StateHolder<S>,
    private val logger: Logger,
    override val coroutineContext: CoroutineContext
) : StateProcessor<S> {

    /**
     * Queue for state reducers.
     * Has unlimited capacity so that sending new elements to it is not a blocking operation
     **/
    private val setStateChannel: Channel<Benchmark_SetStateAction<S>> = Channel(Channel.UNLIMITED)

    /**
     * Queue for actions on the current state.
     * Has unlimited capacity so that sending new elements to it is not a blocking operation
     **/
    private val getStateChannel: Channel<Benchmark_GetStateAction<S>> = Channel(Channel.UNLIMITED)

    init {
        if (isLazy) {
            logger.logv { "Starting in Lazy mode. Call start() to begin processing actions and reducers" }
        } else {
            start()
        }
    }

    /**
     * Enqueues the given [reducer] to an internal queue
     */
    override fun offerSetAction(reducer: suspend S.() -> S) {
        setStateChannel.offer(Benchmark_SetStateAction(reducer))
    }

    /**
     * Enqueues the given [action] to an internal queue
     */
    override fun offerGetAction(action: suspend (S) -> Unit) {
        getStateChannel.offer(Benchmark_GetStateAction(action))
    }

    /**
     * Cancels this processor's coroutine scope and stops processing of jobs.
     */
    override fun clearProcessor() {
        logger.logd { "Clearing StateProcessor $this" }
        this.cancel()
    }

    /**
     * Launches a coroutine to start processing jobs sent to it.
     */
    internal fun start() {
        launch {
            while (isActive) {
                select<Unit> {
                    setStateChannel.onReceive { action ->
                        val newState = action.reducer(stateHolder.state)
                        stateHolder.updateState(newState)
                    }
                    getStateChannel.onReceive { action ->
                        action.block(stateHolder.state)
                    }
                }
            }
        }
    }
}
