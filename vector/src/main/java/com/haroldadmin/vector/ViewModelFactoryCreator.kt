package com.haroldadmin.vector

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.haroldadmin.vector.loggers.Logger
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

/**
 * Creates and returns an instance of a [ViewModelProvider.Factory] which can instantiate a [VectorViewModel].
 * This factory can be used with a [ViewModelProvider] to get a registered instance of a ViewModel.
 */
internal interface ViewModelFactoryCreator {

    fun <VM : VectorViewModel<S>, S : VectorState> create(
        vmClass: KClass<out VM>,
        stateClass: KClass<out S>,
        viewModelOwner: ViewModelOwner,
        savedStateRegistryOwner: SavedStateRegistryOwner,
        stateStoreContext: CoroutineContext,
        logger: Logger
    ): ViewModelProvider.Factory
}

/**
 * Creates and returns an instance of a [ViewModelProvider.Factory] which creates a [VectorViewModel] by trying to
 * instantiate it from its constructor
 *
 * The [VectorViewModel] must have one of the following constructors for it to be instantiated automatically:
 * 1. ViewModel(initialState)
 * 2. ViewModel(initialState, savedStateHandle)
 * 3. ViewModel(initialState, stateStoreContext, logger)
 * 4. ViewModel(initialState, stateStoreContext, logger, savedStateHandle)
 *
 * If it does not have one of these constructors, it should implement a [VectorViewModelFactory] in its companion object
 * which creates this view model and returns it.
 */
internal object ConstructorStrategyVMFactoryCreator : ViewModelFactoryCreator {

    override fun <VM : VectorViewModel<S>, S : VectorState> create(
        vmClass: KClass<out VM>,
        stateClass: KClass<out S>,
        viewModelOwner: ViewModelOwner,
        savedStateRegistryOwner: SavedStateRegistryOwner,
        stateStoreContext: CoroutineContext,
        logger: Logger
    ): ViewModelProvider.Factory {

        val stateFactory: VectorStateFactory = RealStateFactory()
        val constructor = vmClass.primaryConstructor
        val parametersSize = constructor?.parameters?.size ?: Int.MAX_VALUE

        if (constructor == null || parametersSize > 4) {
            throw NoSuitableViewModelConstructorException()
        }

        return VectorSavedStateViewModelFactory(savedStateRegistryOwner, null) { _, handle ->
            val initialState = stateFactory.createInitialState(vmClass, stateClass, handle, viewModelOwner)

            @Suppress("UNCHECKED_CAST")
            when (parametersSize) {
                0 -> vmClass.java.newInstance()
                1 -> vmClass.java.instance(initialState) as VM
                2 -> constructor.call(initialState, handle)
                3 -> constructor.call(initialState, stateStoreContext, logger)
                4 -> constructor.call(initialState, stateStoreContext, logger, handle)
                else -> throw IllegalStateException("Unable to satisfy given ViewModel's constructor")
            }
        }
    }
}

/**
 * Creates and returns an instance of a [VectorViewModel] using its companion object implementing the interface
 * [VectorViewModelFactory].
 */
internal object FactoryStrategyVMFactoryCreator : ViewModelFactoryCreator {

    override fun <VM : VectorViewModel<S>, S : VectorState> create(
        vmClass: KClass<out VM>,
        stateClass: KClass<out S>,
        viewModelOwner: ViewModelOwner,
        savedStateRegistryOwner: SavedStateRegistryOwner,
        stateStoreContext: CoroutineContext,
        logger: Logger
    ): ViewModelProvider.Factory {

        val companionFactoryClass = vmClass.factoryKompanion()

        if (companionFactoryClass doesImplement VectorViewModelFactory::class && companionFactoryClass doesOverride "create") {

            @Suppress("UNCHECKED_CAST")
            return VectorSavedStateViewModelFactory(savedStateRegistryOwner, null) { _, handle ->
                val creationMethod = companionFactoryClass.java.getMethod("create", stateClass.java, ViewModelOwner::class.java, SavedStateHandle::class.java)
                val stateFactory: VectorStateFactory = RealStateFactory()
                val initialState = stateFactory.createInitialState(vmClass, stateClass, handle, viewModelOwner)
                creationMethod.invoke(companionFactoryClass.java.instance(), initialState, viewModelOwner, handle) as? VM
            }
        } else {
            throw DoesNotImplementVectorVMFactoryException()
        }
    }
}
