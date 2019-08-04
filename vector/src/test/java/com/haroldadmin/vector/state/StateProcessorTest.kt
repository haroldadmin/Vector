package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.StringLogger
import com.haroldadmin.vector.loggers.systemOutLogger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test

class StateProcessorTest {

    private val testScope = TestCoroutineScope()

    private val initialState = CountingState()
    private val stateHolder = StateHolderFactory.create(initialState, systemOutLogger())
    val job = Job()
    private val stateProcessor =
        StateProcessorFactory.create(stateHolder, systemOutLogger(), testScope.coroutineContext + job)

    @Test
    fun givenStateProcessor_whenCleared_shouldCancelCoroutineContextJob() =
        testScope.runBlockingTest {
            stateProcessor.clearProcessor()
            assert(job.isCancelled)
        }

    @Test
    fun givenStateProcessor_whenNewStateIsSet_thenItShouldBeSendToStateHolder() =
        testScope.runBlockingTest {
            val deferred = CompletableDeferred<Unit>()
            stateProcessor.offerSetAction {
                deferred.complete(Unit)
                copy(count = this.count + 1)
            }

            deferred.await()
            assert(stateHolder.state.count == initialState.count + 1)
        }

    @Test
    fun givenStateProcessor_whenSetStateAndGetStateBlocksAreBothEnqueued_thenSetStateBlockPrioritized() =
        testScope.runBlockingTest {
            val setStateDeferred = CompletableDeferred<Unit>()
            val getStateDeferred = CompletableDeferred<Unit>()
            // Whichever block completes second will have no effect on the value of this deferred
            val completedFirstValueDeferred = CompletableDeferred<String>()

            pauseDispatcher()

            stateProcessor.offerGetAction {
                getStateDeferred.complete(Unit)
                completedFirstValueDeferred.complete("Get-State-Block")
            }
            stateProcessor.offerSetAction {
                setStateDeferred.complete(Unit)
                completedFirstValueDeferred.complete("Set-State-Block")
                this
            }

            runCurrent()

            setStateDeferred.await()
            getStateDeferred.await()
            val completedFirstValue = completedFirstValueDeferred.await()

            assert(completedFirstValue == "Set-State-Block")
        }

    @Test
    fun stateConsistencyTest() = testScope.runBlockingTest {
        val mutationActions = 10
        val deferreds =
            Array<CompletableDeferred<Unit>>(2 * mutationActions) { CompletableDeferred() }

        repeat(mutationActions) { position ->
            stateProcessor.offerSetAction {
                deferreds[position].complete(Unit)
                copy(count = this.count + 1)
            }
        }

        val offset = mutationActions
        repeat(mutationActions) { position ->
            stateProcessor.offerSetAction {
                deferreds[position + offset].complete(Unit)
                copy(count = this.count - 1)
            }
        }

        deferreds.forEach { it.await() }

        assert(stateHolder.state.count == 0)
    }

    @Test
    fun givenStateProcessor_whenStateIsMutated_thenNewStateShouldBePushedToStateObservable() =
        testScope.runBlockingTest {
            repeat(10) { i ->
                val deferred = CompletableDeferred<Unit>()
                stateProcessor.offerSetAction {
                    deferred.complete(Unit)
                    copy(count = i)
                }
                deferred.await()
                val stateCount = stateHolder.stateObservable.value.count
                assert(stateCount == i)
            }
        }

    @Test(expected = IllegalStateException::class)
    fun givenStateProcessorWithoutInitialStateInStateHolder_whenStateIsMutated_thenShouldThrowError() = testScope.runBlockingTest {
        val holder = StateHolderFactory.create<CountingState>(StringLogger())
        val processor = StateProcessorFactory.create(holder, StringLogger(), testScope.coroutineContext)

        processor.offerSetAction { copy(count = this.count + 1) }
    }
}