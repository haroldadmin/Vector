package com.haroldadmin.vector

import android.os.Build
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.loggers.systemOutLogger
import com.haroldadmin.vector.state.CountingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
internal class VectorViewModelProviderTest {

    private lateinit var activity: ProviderTestActivity
    private val stateStoreContext = Dispatchers.Default + Job()

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(ProviderTestActivity::class.java).setup().get()
    }

    @Test
    fun `creation of ViewModel with Factory implementing the initialState function`() {
        val vm = VectorViewModelProvider.createViewModel(
            VMWithFactoryImplementingInitialState::class,
            CountingState::class,
            activity.activityViewModelOwner(),
            activity,
            stateStoreContext,
            systemOutLogger()
        )

        withState(vm) { state -> assert(state == VMWithFactoryImplementingInitialState.initialStateForTesting) }
    }

    @Test
    fun `creation of ViewModel with Factory not implementing the initialState function`() {
        val vm = VectorViewModelProvider.createViewModel(
            VMWithFactoryWithoutInitialStateMethod::class,
            CountingState::class,
            activity.activityViewModelOwner(),
            activity,
            stateStoreContext,
            systemOutLogger()
        )
        withState(vm) { state -> assert(state == CountingState()) }
    }

    @Test
    fun `creation of SavedStateViewModel without a factory test`() {
        val vm = VectorViewModelProvider.createViewModel(
            TestSavedStateViewModel::class,
            CountingState::class,
            activity.activityViewModelOwner(),
            activity,
            stateStoreContext,
            systemOutLogger()
        )

        assert(vm.currentState == CountingState())
    }

    @Test
    fun `creation of SavedStateViewModel with a factory test`() {
        val vm = VectorViewModelProvider.createViewModel(
            TestSavedStateViewModelWithFactory::class,
            CountingState::class,
            activity.activityViewModelOwner(),
            activity,
            stateStoreContext,
            systemOutLogger()
        )

        assert(vm.currentState == CountingState())
    }

    @Test
    fun `creation of ViewModel with multiple params test`() {
        val vm = VectorViewModelProvider.createViewModel(
            TestViewModelWithMultipleParams::class,
            CountingState::class,
            activity.activityViewModelOwner(),
            activity,
            stateStoreContext,
            systemOutLogger()
        )

        assert(vm.currentState == CountingState())
    }

    @Test
    fun `creation of ViewModel with a producer`() {
        val viewModel = activity.viewModel<VMWithProducer, CountingState> { initialState, handle -> VMWithProducer.produce(initialState, handle) }.value
        withState(viewModel) { state -> assert(state == CountingState()) }
    }

    @Test
    fun `ViewModel with companion factory without create method creation`() {
        val vm = VectorViewModelProvider.get(
            VMWithFactoryWithoutCreateMethod::class,
            CountingState::class,
            activity,
            activity.activityViewModelOwner(),
            stateStoreContext,
            systemOutLogger()
        )

        withState(vm) { state -> assert(state.count == 42) }
    }
}

private class ProviderTestActivity : FragmentActivity()

private class VMWithFactoryImplementingInitialState(
    initialState: CountingState
) : VectorViewModel<CountingState>(initialState) {
    companion object : VectorViewModelFactory<VMWithFactoryImplementingInitialState, CountingState> {
        val initialStateForTesting = CountingState(42)
        override fun initialState(handle: SavedStateHandle, owner: ViewModelOwner): CountingState? {
            return initialStateForTesting
        }
        override fun create(initialState: CountingState, owner: ViewModelOwner, handle: SavedStateHandle): VMWithFactoryImplementingInitialState {
            return VMWithFactoryImplementingInitialState(initialState)
        }
    }
}

private class VMWithFactoryWithoutInitialStateMethod(
    initialState: CountingState
) : VectorViewModel<CountingState>(initialState) {
    companion object : VectorViewModelFactory<VMWithFactoryWithoutInitialStateMethod, CountingState> {
        override fun create(initialState: CountingState, owner: ViewModelOwner, handle: SavedStateHandle): VMWithFactoryWithoutInitialStateMethod {
            return VMWithFactoryWithoutInitialStateMethod(initialState)
        }
    }
}

private class VMWithProducer(
    initialState: CountingState,
    handle: SavedStateHandle
) : VectorViewModel<CountingState>(initialState) {
    companion object {
        fun produce(initialState: CountingState, handle: SavedStateHandle): VMWithProducer {
            return VMWithProducer(initialState, handle)
        }
    }
}

private class VMWithFactoryWithoutCreateMethod : VectorViewModel<CountingState>(CountingState(42)) {
    companion object: VectorViewModelFactory<VMWithFactoryWithoutCreateMethod, CountingState>
}