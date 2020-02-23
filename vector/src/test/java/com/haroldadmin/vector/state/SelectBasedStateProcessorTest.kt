package com.haroldadmin.vector.state

import com.haroldadmin.vector.Vector
import com.haroldadmin.vector.extensions.awaitCompletion
import com.haroldadmin.vector.loggers.systemOutLogger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.ClosedSendChannelException
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test

internal class SelectBasedStateProcessorTest {

    private lateinit var holder: StateHolder<CountingState>
    private lateinit var processor: SelectBasedStateProcessor<CountingState>

    @Before
    fun setup() {
        holder = StateHolderFactory.create(CountingState(), systemOutLogger())
        processor = SelectBasedStateProcessor(
            isLazy = true,
            stateHolder = holder,
            logger = systemOutLogger(),
            coroutineContext = Dispatchers.Unconfined + Job()
        )
    }

    @After
    fun clear() {
        processor.clearProcessor()
        holder.clearHolder()
    }

    @Test
    fun `when new state is set, it should store it to state holder`() = runBlocking {
        processor.start()
        awaitCompletion<Unit> {
            processor.offerSetAction {
                copy(count = 42).also { complete(Unit) }
            }
        }
        assert(holder.state.count == 42)
    }

    @Test
    fun `when multiple jobs are enqueued, should process state reducers before actions`() = runBlocking {
        val reducerValue = "reducer-first"
        val actionValue = "action-first"

        val valueSetFirst = awaitCompletion<String> {
            // Processor is started in lazy mode, jobs are not being processed yet. They are just being enqueued.
            processor.offerGetAction {
                complete(actionValue)
            }
            processor.offerSetAction {
                val newState = copy(count = 42)
                complete(reducerValue)
                newState
            }
            processor.start()
        }

        assert(valueSetFirst == reducerValue)
    }

    @Test
    fun `when a job produces more reducers, then they should be processed before any actions`() = runBlocking {
        val secondReducerValue = "reducer-second"
        val secondActionValue = "action-second"

        val valueSetFirst = awaitCompletion<String> {
            processor.offerSetAction {
                processor.offerGetAction {
                    complete(secondActionValue)
                }
                processor.offerSetAction {
                    complete(secondReducerValue)
                    this
                }
                this
            }
            processor.start()
        }

        assert(valueSetFirst == secondReducerValue)
    }

    @Test
    fun `state should remain consistent even when there are multiple sources of jobs`() = runBlocking {

        val iterations = 10
        val additionJobsCompletable = CompletableDeferred<Unit>()
        val subtractionJobsCompletable = CompletableDeferred<Unit>()

        val incrementActionsSourceJob = async {
            repeat(iterations) { i ->
                processor.offerSetAction {
                    copy(count = count + 1).also { if (i == iterations - 1) additionJobsCompletable.complete(Unit) }
                }
            }
        }

        val decrementActionsSourceJob = async {
            repeat(iterations) { i ->
                processor.offerSetAction {
                    copy(count = count - 1).also { if (i == iterations - 1) subtractionJobsCompletable.complete(Unit) }
                }
            }
        }

        processor.start()

        awaitAll(
            incrementActionsSourceJob,
            decrementActionsSourceJob,
            additionJobsCompletable,
            subtractionJobsCompletable
        )

        assert(holder.state.count == 0)
    }

    @Test()
    fun `jobs sent after processor is cleared should be ignored`() = runBlocking {
        processor.start()
        processor.clearProcessor()
        var count = 0
        processor.offerGetAction {
            count++
        }

        processor.offerSetAction {
            count++
            this
        }

        assert(count == 0) {
            """Count value changed when it should not have been. Expected 0, got $count
                |State Processor is not ignoring jobs sent to it after it has been cleared""".trimMargin()
        }
    }

    @Test
    fun `clear operation should be idempotent`() = runBlocking {
        processor.start()
        repeat(10) { processor.clearProcessor() }
    }

    @Test
    fun `should not wait for get-state actions to complete before processing the next action`() = runBlocking {
        val initialCount = holder.state.count + 1
        processor.start()

        processor.offerGetAction {
            delay(1000L)
        }

        awaitCompletion<Unit> {
            processor.offerSetAction {
                copy(count = initialCount + 1).also { complete(Unit) }
            }
        }

        assert(holder.state.count == initialCount + 1) {
            "Expected count = ${initialCount + 1}, actual: ${holder.state.count}"
        }
    }

    @Test
    fun `should not access state from StateHolder if it has been cancelled`() = runBlocking {
        holder.clearHolder()
        processor.start()
        processor.offerGetAction { state ->
            // No-op
        }
        processor.offerSetAction {
            copy(count = count + 1)
        }
        // If there are no errors, test is successful
    }
}