package com.haroldadmin.vector.test

import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.Executors

internal data class CountingState(val count: Int = 0) : VectorState

internal class StateStoreTest {

    private val dispatcher = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
    private val testContext = dispatcher + Job()
    private val testScope = CoroutineScope(testContext)
    private val stateStore = CompletableStateStore(testContext, CountingState())

    @Test
    fun simpleSetStateTest() = runBlocking<Unit>(context = testContext) {
        stateStore.set { copy(count = 10) }
        val deferred = stateStore.completableGet { state ->
            assertEquals(10, state.count)
        }
        deferred.await()
    }

    @Test
    fun stateConsistencyTest() = runBlocking<Unit>(context = testContext) {
        testScope.launch {
            repeat(1000) {
                stateStore.set { copy(count = this.count + 1) }
            }

            repeat(1000) {
                stateStore.set { copy(count = this.count + 1) }
            }
        }

        stateStore.completableGet { state ->
            assertEquals(0, state.count)
        }.await()
    }

    @Test
    fun observableStateTest() = runBlocking<Unit> {
        testScope.launch {
            for (i in 1..1000) {
                stateStore.set { copy(count = i) }
            }
        }

        stateStore.stateChannel
            .asFlow()
            .takeWhile { state -> state.count <= 1000 }
            .fold(0) { acc, state ->
                assertTrue(acc <= state.count)
                state.count
            }
    }

    @Test
    fun cleanupTest() {
        stateStore.cleanup()
        assertFalse(stateStore.isActive)
        assertTrue(stateStore.stateChannel.isClosedForSend)
    }

    @After
    fun tearDown() {
        dispatcher.close()
        testScope.cancel()
    }
}