package com.haroldadmin.vector

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith

@Ignore("Possible bug in Fragment Scenario component. Tests fails because fragment crashes while launching")
@RunWith(AndroidJUnit4::class)
class VectorViewModelWithFragmentTest {

    private val vmFactory = TestViewModelFactory()
    private val fragmentFactory = TestFragmentFactory(vmFactory)

    @Test
    fun observedStateCleanupTest() {

        val scenario = launchFragmentInContainer<TestFragment>(factory = fragmentFactory)

        scenario.onFragment { testFragment ->
            testFragment.viewModel.addBlocking()
            assertEquals(testFragment.viewModel.currentState, testFragment.state)
        }

        scenario.moveToState(Lifecycle.State.DESTROYED)

        scenario.onFragment { testFragment ->
            assertTrue(testFragment.viewModel.stateChannel.isClosedForSend)
        }
        Unit
    }

}