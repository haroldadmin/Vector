package com.haroldadmin.vector

import android.os.Build
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.systemOutLogger
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.coroutines.CoroutineContext

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.P])
internal class ViewModelCreatorsTest {

    private val testScope = TestCoroutineScope()
    private val constructorCreator: ViewModelFactoryCreator = ConstructorStrategyVMFactoryCreator
    private val factoryCreator: ViewModelFactoryCreator = FactoryStrategyVMFactoryCreator

    private lateinit var activity: CreationTestActivity

    @Before
    fun setup() {
        Vector.enableLogging = true
        activity = Robolectric.buildActivity(CreationTestActivity::class.java).setup().get()
    }

    @Test
    fun `ViewModel with only initial state param creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                OneParamViewModel::class,
                CreationTestState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext,
                systemOutLogger()
            )
        }
    }

    @Test
    fun `ViewModel with initial state and state handle params creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                TwoParamViewModel::class,
                CreationTestState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext,
                systemOutLogger()
            )
        }
    }

    @Test
    fun `ViewModel with three params creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                ThreeParamViewModel::class,
                CreationTestState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext,
                systemOutLogger()
            )
        }
    }

    @Test
    fun `ViewModel with four params creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                FourParamViewModel::class,
                CreationTestState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext,
                systemOutLogger()
            )
        }
    }

    @Test(expected = NoSuitableViewModelConstructorException::class)
    fun `ViewModel with five params creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                FiveParamViewModel::class,
                CreationTestState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext,
                systemOutLogger()
            )
        }
    }

    @Test
    fun `ViewModel with companion factory creation using factory`() {
        with(activity) {
            factoryCreator.create(
                ViewModelWithFactory::class,
                CreationTestState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext,
                systemOutLogger()
            )
        }
    }

    private data class CreationTestState(val count: Int = 0) : VectorState

    private class OneParamViewModel(
        initialState: CreationTestState
    ) : VectorViewModel<CreationTestState>(initialState)

    private class TwoParamViewModel(
        initialState: CreationTestState,
        savedStateHandle: SavedStateHandle
    ) : SavedStateVectorViewModel<CreationTestState>(initialState = initialState, savedStateHandle = savedStateHandle)

    private class ThreeParamViewModel(
        initialState: CreationTestState,
        stateStoreContext: CoroutineContext,
        logger: Logger
    ) : VectorViewModel<CreationTestState>(initialState, stateStoreContext, logger)

    private class FourParamViewModel(
        initialState: CreationTestState,
        stateStoreContext: CoroutineContext,
        logger: Logger,
        savedStateHandle: SavedStateHandle
    ) : SavedStateVectorViewModel<CreationTestState>(initialState, stateStoreContext, logger, savedStateHandle)

    private class FiveParamViewModel(
        initialState: CreationTestState,
        stateStoreContext: CoroutineContext,
        logger: Logger,
        savedStateHandle: SavedStateHandle,
        ignore: Unit = Unit
    ) : SavedStateVectorViewModel<CreationTestState>(initialState, stateStoreContext, logger, savedStateHandle)

    private class ViewModelWithFactory(
        initialState: CreationTestState
    ) : VectorViewModel<CreationTestState>(initialState) {
        companion object : VectorViewModelFactory<ViewModelWithFactory, CreationTestState> {
            override fun create(
                initialState: CreationTestState,
                owner: ViewModelOwner,
                handle: SavedStateHandle
            ): ViewModelWithFactory {
                return ViewModelWithFactory(initialState)
            }
        }
    }

    private class CreationTestActivity : FragmentActivity()
}
