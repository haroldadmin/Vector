package com.haroldadmin.vector.test

import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

internal data class CountingState(val count: Int = 0) : VectorState

internal class StateStoreTest {

    private val testScope = TestCoroutineScope()
    private val mainThreadSurrogate = newSingleThreadContext("Main thread")
    private val stateStore = CompletableStateStore(testScope.coroutineContext, CountingState())

    @Before
    fun setup() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @Test
    fun simpleSetStateTest() = testScope.runBlockingTest {
        stateStore
            .completableSet { copy(count = 10) }
            .await()
        stateStore
            .completableGet { state -> assert(10 == state.count) }
            .await()
    }

    @Test
    fun stateConsistencyTest() = testScope.runBlockingTest {

        val deferredList = mutableListOf<Deferred<Unit>>()

        repeat(10) {
            deferredList += stateStore.completableSet {
                copy(count = this.count + 1)
            }
        }

        repeat(10) {
            deferredList += stateStore.completableSet {
                copy(count = this.count - 1)
            }
        }

        deferredList.forEach { it.await() }

        stateStore
            .completableGet { assert(it.count == 0) }
            .await()
    }

    @Test
    fun observableStateTest() = testScope.runBlockingTest {
        repeat(10) { i ->
            stateStore.completableSet { copy(count = i) }.await()
            val stateCount = stateStore.stateChannel.value.count
            assert(stateCount == i)
        }
    }

    @Test
    fun cleanupTest() {
        /*
        Create a separate state store here because the existing state store is created using TestCoroutineScope
        which does not have a job and therefore throws an exception when cancelled */
        val stateStore = CompletableStateStore(
            initialState = CountingState(),
            coroutineContext = mainThreadSurrogate + Job()
        )

        stateStore.cleanup()
        assert(!stateStore.isActive)
        assert(stateStore.stateChannel.isClosedForSend)
    }

    @After
    fun cleanUp() {
        testScope.cleanupTestCoroutines()
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }
}