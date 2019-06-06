package com.haroldadmin.vector

import com.haroldadmin.vector.viewModel.VectorViewModel

internal data class TestState(val count: Int = 0) : VectorState

internal class TestViewModel(
    initState: TestState
) : VectorViewModel<TestState>(initState) {

    // Exposed publicly for testing
    val stateChannel = stateStore.stateChannel

    // Exposed publicly for testing
    fun publicWithState(block: suspend (TestState) -> Unit) = withState(block)

    fun add(times: Int = 10) {
        repeat(times) {
            setState { copy(count = (currentState.count + 1)) }
        }
    }

    fun subtract(times: Int = 10) {
        repeat(times) {
            setState { copy(count = (currentState.count - 1)) }
        }
    }

    // Exposed publicly to test resource cleanup
    fun clear() {
        super.onCleared()
    }
}