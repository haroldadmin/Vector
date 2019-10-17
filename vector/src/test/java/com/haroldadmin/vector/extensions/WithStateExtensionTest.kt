package com.haroldadmin.vector.extensions

import com.haroldadmin.vector.VectorViewModel
import com.haroldadmin.vector.state.CountingState
import com.haroldadmin.vector.withState
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class WithStateExtensionTest {

    private val testScope = TestCoroutineScope()
    private val mainThreadDispatcher = newSingleThreadContext("Main thread")

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadDispatcher)
    }

    @Test
    fun withStateTest() = testScope.runBlockingTest {

        val deferred = CompletableDeferred<Unit>()
        val initState = CountingState()

        val viewModel = object : VectorViewModel<CountingState>(
            initialState = initState,
            stateStoreContext = testScope.coroutineContext + Job()
        ) {
            fun incrementCount() = setState {
                val newState = copy(count = this.count + 1)
                deferred.complete(Unit)
                newState
            }
        }

        withState(viewModel) { state ->
            assert(initState == state)
        }

        viewModel.incrementCount()
        deferred.await()

        withState(viewModel) { state ->
            assert(state.count == initState.count + 1)
        }
    }

    @After
    fun cleanup() {
        testScope.cleanupTestCoroutines()
        Dispatchers.resetMain()
        mainThreadDispatcher.close()
    }
}