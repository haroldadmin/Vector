package com.haroldadmin.vector.benchmark.stateProcessor

import com.haroldadmin.vector.benchmark.TestState
import com.haroldadmin.vector.state.StateHolder
import kotlinx.coroutines.channels.ConflatedBroadcastChannel

internal class TestStateHolder : StateHolder<TestState> {
    override val stateObservable: ConflatedBroadcastChannel<TestState> = ConflatedBroadcastChannel(TestState())

    override fun clearHolder() {
        stateObservable.close()
    }
}