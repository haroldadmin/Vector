package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.StringLogger
import org.junit.Test

class StateHolderTest {

    @Test
    fun `StateHolderFactory creates correctly configured StateHolder instance`() {

        val initState = CountingState()

        val stateHolder = StateHolderFactory.create(
            initialState = initState,
            logger = StringLogger()
        )

        assert(stateHolder.state == initState)
    }

    @Test(expected = IllegalStateException::class)
    fun `StateHolder created without an initial state should have no initial state`() {
        val stateHolder = StateHolderFactory.create<CountingState>(StringLogger())

        stateHolder.state
    }

    @Test
    fun `Clearing StateHolder closes state channel`() {
        val stateHolder = StateHolderFactory.create(CountingState(), StringLogger())
        stateHolder.clearHolder()

        assert(stateHolder.stateObservable.isClosedForSend)
    }
}