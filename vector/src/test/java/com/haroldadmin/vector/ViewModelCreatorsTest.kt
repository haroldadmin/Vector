package com.haroldadmin.vector

import android.os.Build
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.state.CountingState
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

    lateinit var activity: CreationTestActivity

    @Before
    fun setup() {
        Vector.enableLogging = true
        activity = Robolectric.buildActivity(CreationTestActivity::class.java).setup().get()
    }

    @Test
    fun `ViewModel with zero params creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                ZeroParamViewModel::class,
                CountingState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext
            ).create(ZeroParamViewModel::class.java)
        }
    }

    @Test
    fun `ViewModel with only initial state param creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                OneParamViewModel::class,
                CountingState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext
            ).create(OneParamViewModel::class.java)
        }
    }

    @Test
    fun `ViewModel with initial state and state handle params creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                TwoParamViewModel::class,
                CountingState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext
            ).create(TwoParamViewModel::class.java)
        }
    }

    @Test
    fun `ViewModel with state and coroutine context params creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                TwoParamViewModelAlt::class,
                CountingState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext
            ).create(TwoParamViewModelAlt::class.java)
        }
    }

    @Test
    fun `ViewModel with three params creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                ThreeParamViewModel::class,
                CountingState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext
            ).create(ThreeParamViewModel::class.java)
        }
    }

    @Test(expected = NoSuitableViewModelConstructorException::class)
    fun `ViewModel with four params creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                FourParamViewModel::class,
                CountingState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext
            ).create(FourParamViewModel::class.java)
        }
    }

    @Test
    fun `ViewModel with companion factory creation using factory`() {
        with(activity) {
            factoryCreator.create(
                ViewModelWithFactory::class,
                CountingState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext
            ).create(ViewModelWithFactory::class.java)
        }
    }

    @Test(expected = NoSuitableViewModelConstructorException::class)
    fun `ViewModel with unsupported params creation using constructor`() {
        with(activity) {
            constructorCreator.create(
                InvalidParamsViewModel::class,
                CountingState::class,
                activityViewModelOwner(),
                this,
                testScope.coroutineContext
            ).create(InvalidParamsViewModel::class.java)
        }
    }

    class ZeroParamViewModel : VectorViewModel<CountingState>(CountingState())

    class OneParamViewModel(
        initialState: CountingState
    ) : VectorViewModel<CountingState>(initialState)

    class TwoParamViewModel(
        initialState: CountingState,
        savedStateHandle: SavedStateHandle
    ) : SavedStateVectorViewModel<CountingState>(initialState = initialState, savedStateHandle = savedStateHandle)

    class TwoParamViewModelAlt(
        initialState: CountingState,
        coroutineContext: CoroutineContext
    ) : VectorViewModel<CountingState>(initialState, coroutineContext)

    class ThreeParamViewModel(
        initialState: CountingState,
        stateStoreContext: CoroutineContext,
        savedStateHandle: SavedStateHandle
    ) : SavedStateVectorViewModel<CountingState>(initialState, stateStoreContext, savedStateHandle)

    class FourParamViewModel(
        initialState: CountingState,
        stateStoreContext: CoroutineContext,
        savedStateHandle: SavedStateHandle,
        ignore: Unit = Unit
    ) : SavedStateVectorViewModel<CountingState>(initialState, stateStoreContext, savedStateHandle)

    class InvalidParamsViewModel(
        initialState: CountingState,
        ignore: Unit
    ) : VectorViewModel<CountingState>(initialState)

    class ViewModelWithFactory(
        initialState: CountingState
    ) : VectorViewModel<CountingState>(initialState) {
        companion object : VectorViewModelFactory<ViewModelWithFactory, CountingState> {
            override fun create(
                initialState: CountingState,
                owner: ViewModelOwner,
                handle: SavedStateHandle
            ): ViewModelWithFactory {
                return ViewModelWithFactory(initialState)
            }
        }
    }

    class CreationTestActivity : FragmentActivity()
}
