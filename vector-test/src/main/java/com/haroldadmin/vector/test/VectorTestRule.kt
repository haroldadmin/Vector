package com.haroldadmin.vector.test

import com.haroldadmin.vector.VectorViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import kotlin.reflect.full.callSuspend
import kotlin.reflect.full.memberFunctions

/**
 * A JUnit test rule which handles delegating [Dispatchers.Main] responsibility to a regular Dispatcher, which can
 * be configured using the [delegateDispatcher] parameter. It also provides a utility method to wait until
 * all coroutines in a [VectorViewModel] have finished processing.
 *
 * Tests for a [VectorViewModel] are expected to call [awaitCompletion] before making assertions on its state
 */
class VectorTestRule(
    private val delegateDispatcher: CoroutineDispatcher = Dispatchers.Unconfined
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(delegateDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }

    suspend fun awaitCompletion(viewModel: VectorViewModel<*>) {
        val drainMethod = viewModel::class.memberFunctions.first { it.name == "drain" }
        drainMethod.callSuspend(viewModel)
    }
}