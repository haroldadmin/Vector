package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.StringLogger
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

    @Test
    fun `state property contains the latest state`() {
        val initState = CountingState()

        val stateHolder = StateHolderFactory.create(
            initialState = initState,
            logger = StringLogger()
        )

        stateHolder.updateState(CountingState(count = 42))
        assert(stateHolder.state.count == 42) {
            "Expected current count to be 42, got ${stateHolder.state.count}"
        }
    }

    @Test
    fun `state updates are conflated`() = runBlocking {
        val initState = CountingState()

        val stateHolder = StateHolderFactory.create(initState, StringLogger())

        val numberOfUpdates = 10
        // Fast producer
        launch {
            for(i in 1..10) {
                val currentState = stateHolder.state
                val newState = currentState.copy(count = i)
                stateHolder.updateState(newState)
            }
            stateHolder.clearHolder()
        }

        var collectedUpdates = 0
        // Slow consumer
        stateHolder
            .stateObservable
            .takeWhile { it.count < numberOfUpdates }
            .collect {
                collectedUpdates++
                delay(1)
            }

        assert(collectedUpdates < numberOfUpdates) {
            "StateUpdates were not conflated, received as many updates as were produced"
        }
    }
}