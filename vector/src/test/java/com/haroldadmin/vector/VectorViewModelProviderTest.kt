package com.haroldadmin.vector

import io.mockk.mockk
import org.junit.Test

class VectorViewModelProviderTest {

    @Test
    fun `creation of ViewModel test`() {
        val vm = VectorViewModelProvider.createViewModel(
            TestViewModelWithFactory::class.java,
            TestStates::class.java,
            TestStates.TestState(0),
            mockk(),
            mockk()
        )

        assert(vm.currentState == TestStates.TestState(0))
    }

}