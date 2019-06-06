package com.haroldadmin.vector

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.takeWhile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.Executors

@RunWith(AndroidJUnit4::class)
internal class VectorViewModelTest {

    private val initialState = TestState(0)
    private val viewModel = TestViewModel(initialState)
    private val testContext = Executors.newSingleThreadExecutor().asCoroutineDispatcher() + Job()
    private val testScope = CoroutineScope(testContext)

    @Test
    fun simpleSetStateTest() = runBlocking(context = testContext) {
        val count = 1
        viewModel.add(times = count)
        delay(100)
        withState(viewModel) { state ->
            assertEquals(count, state.count)
        }
    }

    @Test
    fun multipleSetStateTest() = runBlocking(context = testContext) {
        val count = 1
        viewModel.add(times = count)
        viewModel.subtract(times = count)
        delay(100)
        withState(viewModel) { state ->
            assertEquals(0, state.count)
        }
    }

    @Test
    fun viewModelCleanupTest() {
        viewModel.clear()
        assertTrue(viewModel.stateChannel.isClosedForSend)
    }

    @Test
    fun stateChannelTest() = runBlocking(context = testContext) {
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
    fun stateChannelAsFlowTest() = runBlocking(context = testContext) {
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
        testScope.cancel()
    }
}