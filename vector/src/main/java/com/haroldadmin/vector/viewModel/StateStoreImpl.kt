package com.haroldadmin.vector.viewModel

import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

/**
 * An Implementation of [StateStore] interface. This class is expected to be owned by a
 * [VectorViewModel] which calls [cleanup] when it is cleared
 *
 * @param initialState The initial state object with which the owning ViewModel was created
 */
internal class StateStoreImpl<S : VectorState>(
    initialState: S
) : StateStore<S> {

    private val executor = Executors.newSingleThreadExecutor()
    private val job = Job()
    private val stateStoreContext = executor.asCoroutineDispatcher() + job
    private val stateStoreScope = CoroutineScope(stateStoreContext)

    /**
     * A [ConflatedBroadcastChannel] to expose the latest value of state to its
     * subscribers
     */
    override val stateChannel = ConflatedBroadcastChannel(initialState)

    override val state: S
        get() = stateChannel.value

    override suspend fun set(action: suspend S.() -> S) = withContext(stateStoreContext) {
        setStateQueue.offer(action)
        flushQueues()
    }

    override suspend fun get(block: suspend (S) -> Unit) = withContext(stateStoreContext) {
        getStateQueue.offer(block)
        flushQueues()
    }

    private val setStateQueue: Channel<suspend S.() -> S> = Channel(capacity = Channel.UNLIMITED)
    private val getStateQueue: Channel<suspend (S) -> Any> = Channel(capacity = Channel.UNLIMITED)

    private suspend fun flushQueues(): Unit = withContext(stateStoreContext) {
        flushSetStateQueue()
        getStateQueue.poll()?.invoke(state) ?: return@withContext
        flushQueues()
    }

    private suspend fun flushSetStateQueue(): Unit = withContext(stateStoreContext) {
        val stateReducer = setStateQueue.poll()
        if (stateReducer != null) {
            val newState = state.stateReducer()
            stateChannel.offer(newState)
        } else {
            return@withContext
        }
        flushSetStateQueue()
    }

    override fun cleanup() {
        job.cancel()
        stateChannel.close()
        setStateQueue.close()
        getStateQueue.close()
        executor.shutdownNow()
    }
}
