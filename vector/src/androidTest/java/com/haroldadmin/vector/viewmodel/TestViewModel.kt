package com.haroldadmin.vector.viewmodel

import com.haroldadmin.vector.Reducer
import com.haroldadmin.vector.VectorAction
import com.haroldadmin.vector.VectorState
import com.haroldadmin.vector.VectorViewModel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

data class TestState(val count: Int = 0) : VectorState

sealed class TestActions: VectorAction {
    object AddAction : TestActions()
    object SubtractAction: TestActions()
    object ResetAction: TestActions()
}

class TestViewModel(initState: TestState?, consumeDelay: Long) : VectorViewModel<TestState, TestActions>(consumeDelay) {

    override val initialState: TestState = initState ?: TestState()

    override val reducer: Reducer<TestState, TestActions> = { action ->
        when (action) {
            TestActions.AddAction -> copy(count = count + 1)
            TestActions.SubtractAction -> copy(count = count - 1)
            TestActions.ResetAction -> copy(count = 0)
        }
    }

    suspend fun add(times: Int = 10, delay: Long = 0) = coroutineScope {
        produce<TestActions.AddAction> {
            repeat(times) { count ->
                dispatch(TestActions.AddAction)
                if (delay > 0) delay(delay)
            }
        }
    }


    suspend fun subtract(times: Int = 10, delay: Long = 0) = coroutineScope {
        produce<TestActions.SubtractAction> {
            repeat(times) {
                dispatch(TestActions.SubtractAction)
                if (delay  > 0) delay(delay)
            }
        }
    }

    suspend fun reset(delay: Long = 0) = coroutineScope {

    }
}