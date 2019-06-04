package com.haroldadmin.vector

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.takeWhile
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VectorViewModelTest {

    private val initialState = TestState(0)
    private val viewModel = TestViewModel(initialState)
    private val testJob = Job()
    private val testScope = CoroutineScope(Dispatchers.Default + testJob)

    @Test
    fun simpleSetStateTest() = runBlocking {
        val count = 1000
        viewModel.add(times = count)
        withState(viewModel) { state ->
            assertEquals(count, state.count)
        }
    }

    @Test
    fun multipleSetStateTest() = runBlocking {
        val count = 1000
        viewModel.add(times = count)
        viewModel.subtract(times = count)

        withState(viewModel) { state ->
            assertEquals(0, state.count)
        }
    }

    @Test
    fun multiThreadedDispatcherSetStateTest() = runBlocking {
        val count = 1000

        withContext(Dispatchers.Unconfined) {
            viewModel.add(times = count)
            viewModel.subtract(times = count)

            withState(viewModel) { state ->
                assertEquals(0, state.count)
            }
        }
    }

    @Test
    fun viewModelCleanupTest() {
        viewModel.clear()
        assertTrue(viewModel.stateChannel.isClosedForSend)
    }

    @Test
    fun stateChannelTest() = runBlocking<Unit> {
        val count = 1000

        var lastState: TestState = initialState

        testScope.launch {
            viewModel.add(times = count)
        }

        viewModel.stateChannel
            .openSubscription()
            .takeWhile { state -> state.count < count }
            .consumeEach { state ->
                assertTrue(lastState.count <= state.count)
                lastState = state
            }
    }

    @Test
    fun stateChannelAsFlowTest() = runBlocking<Unit> {
        val count = 1000
        var lastState = initialState

        testScope.launch {
            viewModel.add(times = count)
        }

        viewModel.stateChannel
            .asFlow()
            .takeWhile { state -> state.count < count }
            .collect { state ->
                assertTrue(lastState.count <= state.count)
                lastState = state
            }
    }

    @After
    fun tearDown() {
        viewModel.clear()
        testJob.cancel()
    }
}