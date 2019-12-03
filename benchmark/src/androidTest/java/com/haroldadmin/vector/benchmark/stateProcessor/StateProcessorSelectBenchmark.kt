package com.haroldadmin.vector.benchmark.stateProcessor

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.haroldadmin.vector.benchmark.TestState
import com.haroldadmin.vector.loggers.androidLogger
import com.haroldadmin.vector.state.Benchmark_SelectBasedStateProcessor
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@Ignore("We don't want benchmarks to run with regular builds")
@RunWith(AndroidJUnit4::class)
internal class StateProcessorSelectBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private lateinit var selectStateProcessor: Benchmark_SelectBasedStateProcessor<TestState>

    @Before
    fun setup() {
        selectStateProcessor = Benchmark_SelectBasedStateProcessor(false, TestStateHolder(), androidLogger("Benchmark"), Dispatchers.Default + Job())
    }

    @Test
    fun setStateTest() = benchmarkRule.measureRepeated {
        runBlocking {
            val deferred = CompletableDeferred<Unit>()
            selectStateProcessor.offerSetAction {
                val newState = copy(count = count + 1)
                deferred.complete(Unit)
                newState
            }
            deferred.await()
        }
    }
}