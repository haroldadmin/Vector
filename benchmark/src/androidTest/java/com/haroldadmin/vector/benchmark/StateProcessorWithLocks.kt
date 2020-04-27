package com.haroldadmin.vector.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
internal class StateProcessorWithoutLocks<S : Any>(
    private val stateHolder: StateHolderWithoutLocks<S>,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {

    private val setStateChannel: Channel<reducer<S>> = Channel(Channel.UNLIMITED)
    private val getStateChannel: Channel<action<S>> = Channel(Channel.UNLIMITED)

    init {
        start()
    }

    fun offerSetAction(reducer: suspend S.() -> S) {
        if (!setStateChannel.isClosedForSend) {
            setStateChannel.offer(reducer)
        }
    }

    fun offerGetAction(action: suspend (S) -> Unit) {
        if (!getStateChannel.isClosedForSend) {
            getStateChannel.offer(action)
        }
    }

    fun clearProcessor() {
        if (isActive && !setStateChannel.isClosedForSend && !getStateChannel.isClosedForSend) {
            this.cancel()
            setStateChannel.close()
            getStateChannel.close()
        }
    }

    private fun start() = launch {
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

@ExperimentalCoroutinesApi
internal class StateProcessorWithLocks<S : Any>(
    private val stateHolder: StateHolderWithLocks<S>,
    override val coroutineContext: CoroutineContext
) : CoroutineScope {

    private val setStateChannel: Channel<reducer<S>> = Channel(Channel.UNLIMITED)
    private val setStateMutex = Mutex()
    private val getStateChannel: Channel<action<S>> = Channel(Channel.UNLIMITED)
    private val getStateMutex = Mutex()

    private val unambiguousThis = this

    init {
        start()
    }

    suspend fun offerSetAction(reducer: suspend S.() -> S) {
        setStateMutex.withLock {
            if (!setStateChannel.isClosedForSend) {
                setStateChannel.offer(reducer)
            }
        }
    }

    suspend fun offerGetAction(action: suspend (S) -> Unit) {
        getStateMutex.withLock {
            if (!getStateChannel.isClosedForSend) {
                getStateChannel.offer(action)
            }
        }
    }

    suspend fun clearProcessor() {
        setStateMutex.withLock {
            getStateMutex.withLock {
                if (isActive && !setStateChannel.isClosedForSend && !getStateChannel.isClosedForSend) {
                    unambiguousThis.cancel()
                    setStateChannel.close()
                    getStateChannel.close()
                }
            }
        }
    }

    private fun start() = launch {
        while (isActive) {
            select<Unit> {
                setStateChannel.onReceive { reducer ->
                    stateHolder.stateObservable.valueOrNull?.let { state ->
                        val newState = state.reducer()
                        stateHolder.sendStateUpdate(newState)
                    }
                }
                getStateChannel.onReceive { action ->
                    launch {
                        action.invoke(stateHolder.getState())
                    }
                }
            }
        }
    }
}

private typealias reducer<S> = suspend S.() -> S

private typealias action<S> = suspend (S) -> Unit

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@Ignore("Benchmark does not start, and we do not want to run benchmarks on regular builds")
internal class StateProcessorWithLocksBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var noLocks: StateProcessorWithoutLocks<TestState>
    private lateinit var locks: StateProcessorWithLocks<TestState>

    private lateinit var coroutineContext: CoroutineContext
    private lateinit var job: Job

    private val repeatCount = 2000

    @Before
    fun setup() {
        job = Job()
        coroutineContext = Dispatchers.Unconfined + job
        noLocks = StateProcessorWithoutLocks(StateHolderWithoutLocks(TestState()), coroutineContext)
        locks = StateProcessorWithLocks(StateHolderWithLocks(TestState()), coroutineContext)
    }

    @Test
    fun benchmarkProcessorWithNoLocks() {
        benchmarkRule.measureRepeated {
            val deferred = CompletableDeferred<Unit>()
            repeat(repeatCount) {
                noLocks.offerSetAction {
                    if (count == repeatCount - 1) {
                        deferred.complete(Unit)
                    }
                    copy(count = count + 1)
                }
            }
            runBlocking {
                deferred.await()
            }
        }
    }

    @Test
    fun benchmarkProcessorWithLocks() {
        benchmarkRule.measureRepeated {
            val deferred = CompletableDeferred<Unit>()
            runBlocking {
                repeat(repeatCount) {
                    locks.offerSetAction {
                        if (count == repeatCount - 1) {
                            deferred.complete(Unit)
                        }
                        copy(count = count + 1)
                    }
                }
                deferred.await()
            }
        }
    }

    @After
    fun cleanup() = runBlocking {
        job.cancelAndJoin()
    }
}
