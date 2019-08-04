package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.StringLogger
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test

class StateStoreTest {

    private val testScope = TestCoroutineScope()
    private val initState = CountingState()
    private val logger = StringLogger()

    @Test(expected = IllegalStateException::class)
    fun `Accessing current state when no initial state was supplied should throw an error`() {
        val stateStore = StateStoreFactory.create<CountingState>(StringLogger(), testScope.coroutineContext)
        stateStore.state
    }

    @Test
    fun `Accessing current state after setting initial state when it was not supplied on creation should not throw an error`() {
        val stateStore = StateStoreFactory.create<CountingState>(StringLogger(), testScope.coroutineContext)
        stateStore.setInitialState(CountingState())

        stateStore.state
    }

    @Test
    fun `Clearing state store should also clear StateHolder and StateProcessor`() {
        val holder = spyk(StateHolderFactory.create(initState, logger))
        val processor = spyk(StateProcessorFactory.create(holder, logger, testScope.coroutineContext + Job()))
        val stateStore = StateStoreFactory.create(holder, processor, logger)

        stateStore.clear()

        verify(exactly = 1) { holder.clearHolder() }
        verify(exactly = 1) { processor.clearProcessor() }
    }
}