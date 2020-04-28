package com.haroldadmin.vector

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.state.CountingState
import com.haroldadmin.vector.state.StateHolder
import com.haroldadmin.vector.state.StateProcessor
import com.haroldadmin.vector.state.StateStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

internal sealed class TestStates : VectorState {
    abstract val count: Int
    data class TestState(override val count: Int) : TestStates()
    data class TestStateWithDefaults(override val count: Int = 42) : TestStates()
}

internal class TestViewModel(initialState: TestStates) : VectorViewModel<TestStates>(initialState)

internal class TestViewModelWithFactory(
    initialState: TestStates,
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job()
) : VectorViewModel<TestStates>(initialState, stateStoreContext) {

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
    initialState: TestStates.TestStateWithDefaults,
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job()
) : VectorViewModel<TestStates.TestStateWithDefaults>(initialState, stateStoreContext) {
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
    initialState: CountingState,
    stateStoreContext: CoroutineContext,
    savedStateHandle: SavedStateHandle
) : SavedStateVectorViewModel<CountingState>(
    initialState,
    stateStoreContext,
    savedStateHandle
)

internal class TestActivity : AppCompatActivity()
internal class TestFragment : Fragment()

// Testing utility to access a VectorViewModel's state store through reflection
internal fun <T : VectorState> VectorViewModel<T>.reflectedStateStore(): StateStore<T> {
    val stateStoreField = this::class.java.getDeclaredField("stateStore")
    stateStoreField.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    val stateStore = stateStoreField.get(this) as StateStore<T>
    stateStoreField.isAccessible = false
    return stateStore
}

// Testing utility to access a VectorViewModel's state holder through reflection
internal fun <T : VectorState> VectorViewModel<T>.reflectedStateHolder(): StateHolder<T> {
    val stateStore = reflectedStateStore()
    val stateHolderField = stateStore::class.java.getDeclaredField("stateHolder")
    stateHolderField.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    val stateHolder = stateHolderField.get(this) as StateHolder<T>
    stateHolderField.isAccessible = false
    return stateHolder
}

// Testing utility to access a VectorViewModel's state processor through reflection
internal fun <T : VectorState> VectorViewModel<T>.stateProcessor(): StateProcessor<T> {
    val stateStore = reflectedStateStore()
    val stateProcessorField = stateStore::class.java.getDeclaredField("stateProcessor")
    stateProcessorField.isAccessible = true
    @Suppress("UNCHECKED_CAST")
    val stateProcessor = stateProcessorField.get(this) as StateProcessor<T>
    stateProcessorField.isAccessible = false
    return stateProcessor
}
