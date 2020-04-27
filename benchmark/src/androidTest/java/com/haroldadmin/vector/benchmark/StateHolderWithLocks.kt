package com.haroldadmin.vector.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
internal class StateHolderWithoutLocks<S : Any>(
    initialState: S
) {
    val stateObservable = ConflatedBroadcastChannel(initialState)

    val state: S
        get() = stateObservable.value

    fun sendStateUpdate(state: S) {
        if (!stateObservable.isClosedForSend) {
            stateObservable.offer(state)
        }
    }

    fun clearHolder() {
        stateObservable.close()
    }
}

@ExperimentalCoroutinesApi
internal class StateHolderWithLocks<S : Any>(
    initialState: S
) {
    private val stateUpdateMutex = Mutex()
    val stateObservable = ConflatedBroadcastChannel(initialState)

    suspend fun getState(): S {
        stateUpdateMutex.withLock {
            return stateObservable.value
        }
    }

    suspend fun sendStateUpdate(state: S) {
        stateUpdateMutex.withLock {
            if (!stateObservable.isClosedForSend) {
                stateObservable.offer(state)
            }
        }
    }

    suspend fun clearHolder() {
        stateUpdateMutex.withLock {
            stateObservable.close()
        }
    }
}

/**
 * Results of this benchmark:
 *
 * OnePlus X ->
 *
 * Using [runBlocking] on both methods:
 * benchmark:        27,860 ns StateHolderWithLocksBenchmark.stateUpdateWithNoLocks
 * benchmark:        29,718 ns StateHolderWithLocksBenchmark.stateUpdateWithLocks
 *
 * Using [runBlocking] only on the one with locks:
 * benchmark:         1,433 ns StateHolderWithLocksBenchmark.stateUpdateWithNoLocks
 * benchmark:        29,018 ns StateHolderWithLocksBenchmark.stateUpdateWithLocks
 *
 *
 * Google Pixel ->
 *
 * Using [runBlocking] on both methods:
 * benchmark:        19,389 ns StateHolderWithLocksBenchmark.stateUpdateWithNoLocks
 * benchmark:        12,063 ns StateHolderWithLocksBenchmark.stateUpdateWithLocks
 * The version with locks is consistently faster here. Not sure why.
 *
 * Using [runBlocking] only on the one with locks:
 * benchmark:         1,018 ns StateHolderWithLocksBenchmark.stateUpdateWithNoLocks
 * benchmark:        18,983 ns StateHolderWithLocksBenchmark.stateUpdateWithLocks
 *
 */
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@Ignore("We don't want to run benchmarks with regular builds")
internal class StateHolderWithLocksBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var noLocks: StateHolderWithoutLocks<TestState>
    private lateinit var locks: StateHolderWithLocks<TestState>

    @Before
    fun setup() {
        noLocks = StateHolderWithoutLocks(TestState())
        locks = StateHolderWithLocks(TestState())
    }

    @Test
    fun testStateUpdateWithNoLocks() {
        benchmarkRule.measureRepeated {
            var newState: TestState? = null
            runWithTimingDisabled {
                val currentState = noLocks.stateObservable.value
                newState = currentState.copy(count = currentState.count + 1)
            }
            noLocks.sendStateUpdate(newState!!)
        }
    }

    @Test
    fun testStateUpdateWithLocks() {
        benchmarkRule.measureRepeated {
            var newState: TestState? = null
            runWithTimingDisabled {
                val currentState = locks.stateObservable.value
                newState = currentState.copy(count = currentState.count + 1)
            }
            runBlocking {
                locks.sendStateUpdate(newState!!)
            }
        }
    }

    @After
    fun cleanup() = runBlocking {
        locks.clearHolder()
        noLocks.clearHolder()
    }
}
