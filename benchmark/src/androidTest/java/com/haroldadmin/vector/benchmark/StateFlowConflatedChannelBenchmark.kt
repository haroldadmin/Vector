package com.haroldadmin.vector.benchmark

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * benchmark:           429 ns StateFlowConflatedChannelBenchmark.conflatedBroadcastChannelUpdates
 * benchmark:           238 ns StateFlowConflatedChannelBenchmark.stateFlowUpdates
 */
@RunWith(AndroidJUnit4::class)
class StateFlowConflatedChannelBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Test
    fun conflatedBroadcastChannelUpdates() {
        val channel = ConflatedBroadcastChannel(TestState())
        val newState = TestState(42)
        benchmarkRule.measureRepeated {
            channel.offer(newState)
        }
    }

    @Test
    fun stateFlowUpdates() {
        val flow = MutableStateFlow(TestState())
        val newState = TestState(42)
        benchmarkRule.measureRepeated {
            flow.value = newState
        }
    }

}