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