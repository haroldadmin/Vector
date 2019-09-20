package com.haroldadmin.vector.benchmark.regularStateStore

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.runBlocking
import org.junit.Ignore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

data class TestState(val count: Int = 0) : VectorState

@Ignore("We don't want benchmarks to run with regular builds")
@RunWith(AndroidJUnit4::class)
class StateStoreBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val stateStore =
        RegularStateStoreImpl(TestState())

    @Test
    fun setStateTest() {
        benchmarkRule.measureRepeated {
            runBlocking {
                stateStore.set { copy(count = this.count + 1) }
            }
        }
    }

    @Test
    fun simpleRecursiveCall() {
        benchmarkRule.measureRepeated {
            runBlocking {
                stateStore.set {
                    stateStore.get { state ->
                        // Do nothing
                    }
                    this
                }
            }
        }
    }

    @Test
    fun complexRecursiveCall() {
        benchmarkRule.measureRepeated {
            runBlocking {
                stateStore.get {
                    stateStore.set {
                        stateStore.set { this }
                        stateStore.get {
                            stateStore.set { this }
                        }
                        this
                    }
                }
            }
        }
    }
}