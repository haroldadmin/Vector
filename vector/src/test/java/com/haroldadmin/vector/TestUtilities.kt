package com.haroldadmin.vector

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.systemOutLogger
import com.haroldadmin.vector.state.CountingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

internal sealed class TestStates : VectorState {
    abstract val count: Int
    data class TestState(override val count: Int) : TestStates()
    data class TestStateWithDefaults(override val count: Int = 42) : TestStates()
}

internal class TestViewModel(initialState: TestStates?) : VectorViewModel<TestStates>(initialState)

internal class TestViewModelWithFactory(
    initialState: TestStates?,
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job(),
    logger: Logger = systemOutLogger()
) : VectorViewModel<TestStates>(initialState, stateStoreContext, logger) {

    companion object : VectorViewModelFactory<TestViewModelWithFactory, TestStates> {

        val initialStateForTesting = TestStates.TestState(0)

        override fun initialState(handle: SavedStateHandle, owner: ViewModelOwner): TestStates? {
            return initialStateForTesting
        }

        override fun create(
            initialState: TestStates,
            owner: ViewModelOwner,
            handle: SavedStateHandle
        ): TestViewModelWithFactory {
            return TestViewModelWithFactory(initialState)
        }
    }
}

internal class TestViewModelWithFactoryAndDefaults(
    initialState: TestStates.TestStateWithDefaults?,
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job(),
    logger: Logger = systemOutLogger()
) : VectorViewModel<TestStates.TestStateWithDefaults>(initialState, stateStoreContext, logger) {
    companion object : VectorViewModelFactory<TestViewModelWithFactoryAndDefaults, TestStates.TestStateWithDefaults> {
        override fun initialState(
            handle: SavedStateHandle,
            owner: ViewModelOwner
        ): TestStates.TestStateWithDefaults? {
            return TestStates.TestStateWithDefaults(count = 0)
        }

        override fun create(
            initialState: TestStates.TestStateWithDefaults,
            owner: ViewModelOwner,
            handle: SavedStateHandle
        ): TestViewModelWithFactoryAndDefaults {
            return TestViewModelWithFactoryAndDefaults(initialState)
        }
    }
}

internal class TestSavedStateViewModel(
    initialState: CountingState
) : VectorViewModel<CountingState>(initialState)

internal class TestSavedStateViewModelWithFactory(
    initialState: CountingState,
    handle: SavedStateHandle
) : SavedStateVectorViewModel<CountingState>(
    initialState = initialState,
    savedStateHandle = handle
) {
    companion object : VectorViewModelFactory<TestSavedStateViewModelWithFactory, CountingState> {
        override fun create(
            initialState: CountingState,
            owner: ViewModelOwner,
            handle: SavedStateHandle
        ): TestSavedStateViewModelWithFactory {
            return TestSavedStateViewModelWithFactory(initialState, handle)
        }
    }
}

internal class TestViewModelWithMultipleParams(
    initialState: CountingState?,
    stateStoreContext: CoroutineContext,
    logger: Logger,
    savedStateHandle: SavedStateHandle
) : SavedStateVectorViewModel<CountingState>(
    initialState,
    stateStoreContext,
    logger,
    savedStateHandle
)

internal class TestActivity : AppCompatActivity()
internal class TestFragment : Fragment()
