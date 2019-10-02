package com.haroldadmin.vector

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.systemOutLogger
import com.haroldadmin.vector.state.CountingState
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.coroutines.CoroutineContext

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
internal class SavedStateVectorViewModelTest {

    private val testScope = TestCoroutineScope()
    private val job = Job()
    private val activity: ComponentActivity = Robolectric.buildActivity(ComponentActivity::class.java).setup().get()
    private val viewModel: TestSavedStateVM by activity.viewModel { initialState, handle ->
        TestSavedStateVM(initialState, testScope.coroutineContext + job, systemOutLogger(), handle)
    }

    @Test
    fun setStateAndPersistTest() {
        viewModel.apply {
            incrementCount()
            val savedState = getSavedState()!!
            withState(viewModel) { state -> assert(state == savedState) }
        }
    }
}

private class TestSavedStateVM(
    initialState: CountingState,
    stateStoreContext: CoroutineContext,
    logger: Logger,
    savedStateHandle: SavedStateHandle
) : SavedStateVectorViewModel<CountingState>(initialState, stateStoreContext, logger, savedStateHandle) {

    fun incrementCount() = setStateAndPersist {
        copy(count = this.count + 1)
    }

    fun getSavedState(): CountingState? {
        return savedStateHandle.get<CountingState>(KEY_SAVED_STATE)
    }
}

private class SavedStateVMActivity : ComponentActivity()