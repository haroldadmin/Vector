package com.haroldadmin.vector

import com.haroldadmin.vector.state.CountingState
import io.mockk.mockk
import org.junit.Test

class VectorViewModelProviderTest {

    @Test
    fun `creation of ViewModel with Factory test`() {
        val vm = VectorViewModelProvider.createViewModel(
            TestViewModelWithFactory::class.java,
            TestStates::class.java,
            TestStates.TestState(0),
            mockk(),
            mockk()
        )

        assert(vm.currentState == TestStates.TestState(0))
    }

    @Test
    fun `creation of ViewModel with a factory test`() {
        val vm = VectorViewModelProvider.createViewModel(
            TestViewModelWithFactory::class.java,
            TestStates::class.java,
            TestStates.TestState(0),
            mockk(),
            mockk()
        )

        assert(vm.currentState.count == 0)
    }

    @Test
    fun `creation of SavedStateViewModel without a factory test`() {
        val vm = VectorViewModelProvider.createViewModel(
            TestSavedStateViewModel::class.java,
            CountingState::class.java,
            CountingState(),
            mockk(),
            mockk()
        )

        assert(vm.currentState == CountingState())
    }

    @Test
    fun `creation of SavedStateViewModel with a factory test`() {
        val vm = VectorViewModelProvider.createViewModel(
            TestSavedStateViewModelWithFactory::class.java,
            CountingState::class.java,
            CountingState(),
            mockk(),
            mockk()
        )

        assert(vm.currentState == CountingState())
    }

    @Test
    fun `creation of ViewModel with multiple params test`() {
        val vm = VectorViewModelProvider.createViewModel(
            TestViewModelWithMultipleParams::class.java,
            CountingState::class.java,
            CountingState(),
            mockk(),
            mockk()
        )

        assert(vm.currentState == CountingState())
    }
}