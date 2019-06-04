package com.haroldadmin.vector

import android.util.Log
import com.haroldadmin.vector.viewModel.VectorViewModel
import kotlinx.coroutines.runBlocking

data class TestState(val count: Int = 0) : VectorState

class TestViewModel(initState: TestState?) : VectorViewModel<TestState>(initState ?: TestState()) {

    // Exposed publicly to test resource cleanup
    val stateChannel = stateStore.stateChannel

    suspend fun add(times: Int = 10) {
        repeat(times) {
            setState { copy(count = (currentState.count + 1)) }
        }
    }

    suspend fun subtract(times: Int = 10) {
        repeat(times) {
            setState { copy(count = (currentState.count - 1)) }
        }
    }

    fun addBlocking() = runBlocking {
        Log.d("TestViewModel", "Setting state")
        setState { copy(count = currentState.count + 1) }
    }

    // Exposed publicly to test resource cleanup
    fun clear() {
        super.onCleared()
    }
}