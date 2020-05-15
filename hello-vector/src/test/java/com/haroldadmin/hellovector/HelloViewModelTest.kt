package com.haroldadmin.hellovector

import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.test.VectorTestRule
import com.haroldadmin.vector.withState
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class HelloViewModelTest {

    @get:Rule
    val vectorTestRule = VectorTestRule()

    private lateinit var viewModel: HelloViewModel

    @Before
    fun setup() {
        viewModel = HelloViewModel(HelloState(), savedStateHandle = SavedStateHandle())
    }

    @Test
    fun `should fetch message when initialized`() = runBlocking {
        val expectedMessage = "Hello, World!"

        viewModel.getMessage(delayDuration = 1000)

        vectorTestRule.awaitCompletion(viewModel)

        withState(viewModel) { state ->
            assert(state.message == expectedMessage) {
                "Expected $expectedMessage, got ${state.message}"
            }
        }
    }
}
