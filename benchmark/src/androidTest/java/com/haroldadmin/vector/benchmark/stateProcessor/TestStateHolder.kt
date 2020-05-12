package com.haroldadmin.vector.benchmark.stateProcessor

import com.haroldadmin.vector.benchmark.TestState
import com.haroldadmin.vector.state.StateHolder
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.MutableStateFlow

internal class TestStateHolder : StateHolder<TestState> {
    override val stateObservable = MutableStateFlow(TestState())

    override fun clearHolder() {
//        stateObservable.close()
    }

    override fun updateState(state: TestState) {
        stateObservable.value = state
    }
}