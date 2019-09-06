package com.haroldadmin.vector

import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.systemOutLogger
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.junit.Before
import org.junit.Test
import kotlin.coroutines.CoroutineContext

internal class VectorStateFactoryTest {

    private lateinit var factory: VectorStateFactory

    @Before
    fun setup() {
        factory = RealStateFactory()
    }

    @Test(expected = UnInstantiableStateClassException::class)
    fun `should fail to create state when there's no ViewModel Companion Factory or Default params`() {
        val state = factory.createInitialState<TestStates.TestState>(
            TestViewModel::class.java,
            TestStates.TestState::class.java,
            mockk()
        )
        assert(state.count == 42)
    }

    @Test
    fun `should create state using default parameters when there is no Companion Factory`() {
        val state = factory.createInitialState<TestStates.TestStateWithDefaults>(
            TestViewModel::class.java,
            TestStates.TestStateWithDefaults::class.java,
            mockk()
        )
        assert(state.count == 42)
    }

    @Test
    fun `should use companion factory to create state when it is present`() {
        val state = factory.createInitialState<TestStates.TestState>(
            TestViewModelWithFactory::class.java,
            TestStates.TestState::class.java,
            mockk()
        )
        assert(state.count == 0)
    }

    @Test
    fun `should prefer companion factory over default params when creating state`() {
        val state = factory.createInitialState<TestStates.TestStateWithDefaults>(
            TestViewModelWithFactoryAndDefaults::class.java,
            TestStates.TestStateWithDefaults::class.java,
            mockk()
        )
        assert(state.count == 0)
    }
}
