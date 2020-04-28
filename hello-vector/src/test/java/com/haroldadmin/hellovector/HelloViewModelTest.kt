package com.haroldadmin.hellovector

import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.withState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class HelloViewModelTest {

    private lateinit var viewModel: HelloViewModel
    private lateinit var testContext: CoroutineContext

    @Before
    fun setup() {
        val mainThreadSurrogate = Executors.newSingleThreadExecutor().asCoroutineDispatcher()
        testContext = mainThreadSurrogate + Job()
        Dispatchers.setMain(mainThreadSurrogate)
        viewModel = HelloViewModel(HelloState(), testContext, SavedStateHandle())
    }

    @Test
    fun `should fetch message when initialized`() = runBlocking(testContext) {
        val expectedMessage = "Hello, World!"
        viewModel.getMessage(delayDuration = 0).join()
        withState(viewModel) { state ->
            assert(state.message == expectedMessage) {
                "Expected $expectedMessage, got ${state.message}"
            }
        }
    }

    @After
    fun cleanup() {
        Dispatchers.resetMain()
    }
}
