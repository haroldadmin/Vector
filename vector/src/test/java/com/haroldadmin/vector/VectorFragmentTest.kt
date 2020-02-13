package com.haroldadmin.vector

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import com.haroldadmin.vector.state.CountingState
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Timer
import kotlin.concurrent.fixedRateTimer

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
internal class VectorFragmentTest {

    @Test
    fun `renderState should stop collecting state updates after view has been destroyed`() {
        // Store fragment instance in a variable to make assertions after it has destroyed
        var fragmentInstance: RendererTestFragment? = null

        val scenario = launchFragmentInContainer<RendererTestFragment>()
        scenario.onFragment { fragment ->
            fragmentInstance = fragment
            runBlocking {
                delay(RendererTestViewModel.initialDelay) // Wait for first state update
                assert(fragment.lastUpdatedState != null) {
                    "State updates are not being collected even after view has been created"
                }
            }
        }

        scenario.moveToState(Lifecycle.State.DESTROYED) // Cancel the viewLifecycle coroutine scope

        fragmentInstance!!.let { fragment ->
            runBlocking {
                val currentCount = fragment.lastUpdatedState!!.count
                delay(RendererTestViewModel.period) // Wait for next state update
                val nextCount = fragment.lastUpdatedState!!.count
                assert(nextCount == currentCount) {
                    "State updates are being collected even after the fragment's view has been destroyed"
                }
            }
        }

        fragmentInstance == null
    }

    internal class RendererTestFragment : VectorFragment() {

        private val viewModel: RendererTestViewModel by fragmentViewModel()
        var lastUpdatedState: CountingState? = null

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            renderState(viewModel) { state -> lastUpdatedState = state }
            return View(requireContext())
        }
    }

    internal class RendererTestViewModel : VectorViewModel<CountingState>(CountingState()) {

        companion object {
            const val initialDelay = 100L
            const val period = 100L
        }

        val timer : Timer = fixedRateTimer(
                name = "countdown",
                daemon = true,
                initialDelay = initialDelay,
                period = period
            ) {
                setState {
                    copy(count = count + 1)
                }
            }

        override fun onCleared() {
            super.onCleared()
            timer.cancel()
        }
    }
}