package com.haroldadmin.vector

import org.junit.Test

class VectorViewModelProviderTest {

    @Test
    fun `creation of ViewModel test`() {
        val vm = VectorViewModelProvider.createViewModel(
            TestViewModelWithFactory::class.java,
            TestStates::class.java,
            TestStates.TestState(0)
        )

        assert(vm.currentState == TestStates.TestState(0))
    }

}