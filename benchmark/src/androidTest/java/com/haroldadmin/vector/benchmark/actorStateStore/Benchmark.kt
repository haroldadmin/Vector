package com.haroldadmin.vector.benchmark.actorStateStore

import androidx.benchmark.BenchmarkRule
import androidx.benchmark.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.haroldadmin.vector.VectorState
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
        ActorsStateStoreImpl(TestState())

    @Test
    fun setStateTest() {
        benchmarkRule.measureRepeated {
            stateStore.set { copy(count = this.count + 1) }
        }
    }

    @Test
    fun simpleRecursiveCall() {
        benchmarkRule.measureRepeated {
            stateStore.set {
                stateStore.get {
                    // Do nothing
                }
                this
            }
        }
    }

    @Test
    fun complexRecursiveCall() {
        benchmarkRule.measureRepeated {
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

    @Test
    fun multiLevelStateOperation() {
        benchmarkRule.measureRepeated {
            stateStore.get { state1 ->
                if (state1.count % 2 == 0) {
                    stateStore.get { state2 ->
                        if (state2.count % 4 == 0) {
                            stateStore.set { copy(count = count + 2) }
                        } else {
                            stateStore.set { copy(count = count + 1) }
                        }
                    }
                } else {
                    stateStore.set { copy(count = count + 2) }
                }
            }
        }
    }
}