package com.haroldadmin.vector.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.haroldadmin.vector.withState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class VectorViewModelTest {


    @Test
    fun synchronizedStateProducerReducerTest() = runBlocking<Unit> {
        val count = 10

        val viewModel = TestViewModel(initState = TestState(0), consumeDelay = 0L)

        launch(Dispatchers.Main) { viewModel.add(delay = 0, times = count) }

        delay(10)

        withState(viewModel) { state ->
            assertEquals(count, state.count)
        }
    }

    /**
     * When the state producer produces new states too quickly,
     * the actions channel would suspend the producer before sending the next element.
     */
    @Test
    fun fastStateProducerSlowReducerTest() = runBlocking {
        val count = 10

        val viewModel = TestViewModel(initState = TestState(0), consumeDelay = 100L)

        launch(Dispatchers.Main) { viewModel.add(delay = 0, times = count) }

        // Enough time to only reduce one state
        delay(10)

        withState(viewModel) { state ->
           assertEquals(1, state.count)
        }
    }

    @Test
    fun multipleStateProducersTest() = runBlocking {
        val count = 10000

        val viewModel = TestViewModel(initState = TestState(0), consumeDelay = 0L)

        launch(Dispatchers.Main) {
            viewModel.add(times = count)
            viewModel.subtract(times = count)
        }

        withState(viewModel) { state ->
            assertEquals(0, state.count)
        }

        Unit
    }
}