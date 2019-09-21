package com.haroldadmin.vector

import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.haroldadmin.vector.state.CountingState
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
internal class ViewModelExtensionsTest {

    private lateinit var activity: DelegateTestActivity
    private lateinit var fragmentOne: DelegateTestActivity.DelegateTestFragmentOne
    private lateinit var fragmentTwo: DelegateTestActivity.DelegateTestFragmentTwo

    @Before
    fun setup() {
        activity = Robolectric.buildActivity(DelegateTestActivity::class.java).setup().get()
        fragmentOne = DelegateTestActivity.DelegateTestFragmentOne()
        fragmentTwo = DelegateTestActivity.DelegateTestFragmentTwo()

        activity.apply {
            addFragment(fragmentOne)
            addFragment(fragmentTwo)
        }
    }

    @Test
    fun fragmentViewModelTest() {
        withState(fragmentOne.unsharedViewModel) { state ->
            assert(state == CountingState())
        }

        withState(fragmentTwo.unsharedViewModel) { state ->
            assert(state == CountingState())
        }

        assert(fragmentOne.unsharedViewModel !== fragmentTwo.unsharedViewModel)
    }

    @Test
    fun activityViewModelDelegateTest() {
        assert(fragmentOne.sharedViewModel === fragmentTwo.sharedViewModel)
        assert(fragmentOne.sharedViewModel.currentState === fragmentTwo.sharedViewModel.currentState)
    }

    private class DelegateTestActivity : FragmentActivity() {

        fun addFragment(fragment: Fragment) {
            supportFragmentManager
                .beginTransaction()
                .add(fragment, null)
                .commitNow()
        }

        /**
         * Making these fragments as nested classes inside a private class instead of standalone private classes to
         * stop fragment manager from throwing an error for non public fragment classes.
         */
        class DelegateTestFragmentOne : Fragment() {
            val unsharedViewModel: DelegateTestViewModel by fragmentViewModel()
            val sharedViewModel: DelegateTestSharedViewModel by activityViewModel()
        }

        class DelegateTestFragmentTwo : Fragment() {
            val unsharedViewModel: DelegateTestViewModel by fragmentViewModel()
            val sharedViewModel: DelegateTestSharedViewModel by activityViewModel()
        }
    }

    private class DelegateTestViewModel : VectorViewModel<CountingState>(CountingState())

    private class DelegateTestSharedViewModel : VectorViewModel<CountingState>(CountingState())
}
