package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.StringLogger
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class StateProcessorTest {

    private val testScope = TestCoroutineScope()

    private val initState = CountingState()

    private val stateHolder = StateHolderFactory.create(initState, StringLogger())

    private val stateProcessor = CompletableStateProcessor(stateHolder, testScope.coroutineContext + Job())

    @Test
    fun `State Processor factory should create correctly configured instance`() {
        val job = Job()
        val processor = StateProcessorFactory.create(
            stateHolder,
            StringLogger(),
            testScope.coroutineContext + job
        )

        processor.clearProcessor()
        assert(job.isCancelled)
    }

    @Test
    fun `Setting new state should produce new state`() = testScope.runBlockingTest {
        stateProcessor.completableSetAction {
            copy(count = initState.count + 1)
        }.await()

        var currentState: CountingState? = null
        stateProcessor.completableGetAction { state ->
            currentState = state
        }.await()

        assert(currentState!!.count == initState.count + 1)
    }

    @Test
    fun `Set-State blocks should run before Get-State blocks`() = testScope.runBlockingTest {
        val deferredSet = stateProcessor.completableSetAction {
            copy(count = initState.count + 1)
        }

        var currentState: CountingState? = null
        val deferredGet = stateProcessor.completableGetAction { state ->
            currentState = state
        }

        deferredGet.await()
        deferredSet.await()

        assert(currentState!!.count == initState.count + 1)
    }

    @Test
    fun stateConsistencyTest() = testScope.runBlockingTest {
        val deferreds = mutableListOf<Deferred<Unit>>()

        repeat(10) {
            deferreds += stateProcessor.completableSetAction {
                copy(count = this.count + 1)
            }
        }

        repeat(10) {
            deferreds += stateProcessor.completableSetAction {
                copy(count = this.count - 1)
            }
        }

        deferreds.forEach { it.await() }

        var currentState: CountingState? = null
        stateProcessor.completableGetAction { state ->
            currentState = state
        }.await()

        assert(currentState!!.count == initState.count)
    }

    @Test
    fun stateObservableTest() = testScope.runBlockingTest {
        repeat(10) { i ->
            stateProcessor.completableSetAction { copy(count = i) }.await()
            val stateCount = stateHolder.stateObservable.value.count
            assert(stateCount == i)
        }
    }

    @Test
    fun stateProcessorCleanupTest() = testScope.runBlockingTest {
        stateProcessor.clearProcessor()

        assert(!stateProcessor.isActive)
    }
}