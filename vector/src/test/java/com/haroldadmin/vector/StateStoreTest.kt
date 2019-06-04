package com.haroldadmin.vector

import com.haroldadmin.vector.viewModel.StateStoreImpl
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEachIndexed
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.takeWhile
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.concurrent.Executors


data class CountingState(val count: Int = 0) : VectorState

class StateStoreTest {

    private val job = Job()
    private val executor = Executors.newSingleThreadExecutor()
    private val stateStore = StateStoreImpl(CountingState())
    private val testScope = CoroutineScope(job + executor.asCoroutineDispatcher())

    @After
    fun tearDown() {
        job.cancel()
        executor.shutdownNow()
    }

    @Test
    fun stateConsistencyTest() = runBlocking<Unit> {
        withContext(Dispatchers.Default) {
            repeat(1000) {
                stateStore.set { copy(count = this.count + 1) }
            }

            repeat(1000) {
                stateStore.set { copy(count = this.count + 1) }
            }

            stateStore.get { state ->
                assertEquals(2000, state.count)
            }
        }
    }

    @Test
    fun observableStateTest() = runBlocking<Unit> {
        testScope.launch {
            for (i in 1..1000) {
                stateStore.set { copy(count = i) }
            }
        }

       stateStore.stateChannel.asFlow()
           .takeWhile { state -> state.count < 1000 }
           .fold(0) { acc, state ->
               assertTrue(acc <= state.count)

               state.count
           }
    }

    @Test
    fun cleanupTest() {
        stateStore.cleanup()
        assertTrue(stateStore.stateChannel.isClosedForSend)
    }
}