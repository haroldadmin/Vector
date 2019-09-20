package com.haroldadmin.vector

import com.haroldadmin.vector.state.CountingState
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.CoroutineContext

internal class VectorViewModelTest {

    private val testScope = TestCoroutineScope()
    private lateinit var viewModel: TestVectorViewModel
    private val initialState = CountingState()
    private val job = Job()

    @Before
    fun setup() {
        viewModel = TestVectorViewModel(initialState, testScope.coroutineContext + job)
    }

    @Test
    fun setStateTest() {
        viewModel.incrementCount()
        withState(viewModel) { state -> assert(state.count == initialState.count + 1) }

        viewModel.decrementCount()
        withState(viewModel) { state -> assert(state.count == initialState.count) }
    }

    @Test
    fun clearTest() {
        viewModel.clear()
        assert(job.isCancelled)
    }
}

private class TestVectorViewModel(
    initialState: CountingState,
    stateStoreContext: CoroutineContext
) : VectorViewModel<CountingState>(initialState, stateStoreContext) {

    fun incrementCount() = setState { copy(count = this.count + 1) }
    fun decrementCount() = setState { copy(count = this.count - 1) }
    fun clear() {
        onCleared()
    }
}