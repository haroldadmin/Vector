package com.haroldadmin.vector

import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.androidLogger
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

/**
 * A class which is responsible for creating ViewModel instances.
 *
 * Instantiation of a ViewModel is first attempted using its companion object if it implements [VectorViewModelFactory].
 * If this fails, then the instantiation using the constructor is attempted. If this fails too, then an error is thrown.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
object VectorViewModelProvider {

    /**
     * Creates the requested ViewModel automatically using reflection, and returns it.
     * The returned ViewModel is already registered with a ViewModelProvider
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun <VM : VectorViewModel<S>, S : VectorState> get(
        vmClass: KClass<out VM>,
        stateClass: KClass<out S>,
        savedStateRegistryOwner: SavedStateRegistryOwner,
        viewModelOwner: ViewModelOwner,
        stateStoreContext: CoroutineContext,
        logger: Logger
    ): VM {
        return createViewModel(vmClass, stateClass, viewModelOwner, savedStateRegistryOwner, stateStoreContext, logger)
    }

    /**
     * Creates and returns the requested ViewModel using the supplied [viewModelProducer] and returns it.
     * The returned ViewModel is already registered with a ViewModelProvider
     */
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun <VM : VectorViewModel<S>, S : VectorState> get(
        vmClass: KClass<out VM>,
        stateClass: KClass<out S>,
        viewModelOwner: ViewModelOwner,
        savedStateRegistryOwner: SavedStateRegistryOwner,
        viewModelProducer: (initialState: S, handle: SavedStateHandle) -> VM
    ): VM {

        val stateFactory: VectorStateFactory = RealStateFactory()

        val factory = VectorSavedStateViewModelFactory(savedStateRegistryOwner, null) { _, handle ->
            val initialState = stateFactory.createInitialState(vmClass, stateClass, handle, viewModelOwner)
            viewModelProducer(initialState, handle)
        }

        return when (viewModelOwner) {
            is ActivityViewModelOwner -> ViewModelProvider(viewModelOwner.activity, factory)
            is FragmentViewModelOwner -> ViewModelProvider(viewModelOwner.fragment, factory)
        }.get(vmClass.java)
    }

    @Suppress("UNCHECKED_CAST")
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun <VM : VectorViewModel<S>, S : VectorState> createViewModel(
        vmClass: KClass<out VM>,
        stateClass: KClass<out S>,
        owner: ViewModelOwner,
        savedStateRegistryOwner: SavedStateRegistryOwner,
        stateStoreContext: CoroutineContext,
        logger: Logger = androidLogger()
    ): VM {
        val viewModelFactory = try {
            FactoryStrategyVMFactoryCreator.create(
                vmClass,
                stateClass,
                owner,
                savedStateRegistryOwner,
                stateStoreContext,
                logger
            )
        } catch (ex: DoesNotImplementVectorVMFactoryException) {
            ConstructorStrategyVMFactoryCreator.create(
                vmClass,
                stateClass,
                owner,
                savedStateRegistryOwner,
                stateStoreContext,
                logger
            )
        } catch (ex: NoSuitableViewModelConstructorException) {
            throw UnInstantiableViewModelException()
        }

        return when (owner) {
            is ActivityViewModelOwner -> ViewModelProvider(owner.activity, viewModelFactory)
            is FragmentViewModelOwner -> ViewModelProvider(owner.fragment, viewModelFactory)
        }.get(vmClass.java)
    }
}

internal class UnInstantiableViewModelException :
    IllegalArgumentException(
        """Your VectorViewModel should have one of the following constructors:
        |1. ViewModel(initialState)
        |2. ViewModel(initialState, savedStateHandle)
        |3. ViewModel(initialState, stateStoreContext, logger)
        |4. ViewModel(initialState, stateStoreContext, logger, savedStateHandle)
        |
        |Or it should implement a VectorViewModelFactory in its companion object.
    """.trimMargin()
    )

internal class DoesNotImplementVectorVMFactoryException :
    Exception("This class's companion object does not implement a VectorViewModelFactory, or it does not override the create method")

internal class NoSuitableViewModelConstructorException : Exception()
