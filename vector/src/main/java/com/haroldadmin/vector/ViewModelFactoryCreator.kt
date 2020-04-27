package com.haroldadmin.vector

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.androidLogger
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

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
        stateStoreContext: CoroutineContext
    ): ViewModelProvider.Factory
}

/**
 * Creates and returns an instance of a [ViewModelProvider.Factory] which creates a [VectorViewModel] by trying to
 * instantiate it from its constructor
 *
 * The [VectorViewModel] must have one of the following constructors for it to be instantiated automatically:
 * 1. ViewModel()
 * 2. ViewModel(initialState)
 * 3. ViewModel(initialState, stateStoreContext)
 * 3. ViewModel(initialState, savedStateHandle)
 * 4. ViewModel(initialState, stateStoreContext, logger)
 * 5. ViewModel(initialState, stateStoreContext, savedStateHandle)
 *
 * If it does not have one of these constructors, it should implement a [VectorViewModelFactory] in its companion object
 * which creates this view model and returns it.
 */
internal object ConstructorStrategyVMFactoryCreator : ViewModelFactoryCreator {

    @Suppress("RemoveExplicitTypeArguments")
    private val supportedSignatures: Array<Array<Class<*>>> = arrayOf(
        arrayOf<Class<*>>(),
        arrayOf<Class<*>>(VectorState::class.java),
        arrayOf<Class<*>>(VectorState::class.java, CoroutineContext::class.java),
        arrayOf<Class<*>>(VectorState::class.java, SavedStateHandle::class.java),
        arrayOf<Class<*>>(VectorState::class.java, CoroutineContext::class.java, SavedStateHandle::class.java),
        arrayOf<Class<*>>(VectorState::class.java, CoroutineContext::class.java, Logger::class.java)
    )

    private infix fun Array<Class<*>>.compareSignature(signatureTwo: Array<Class<*>>): Boolean {
        if (this.size != signatureTwo.size) return false

        for (i in this.indices) {
            val paramOne = this[i]
            val paramTwo = signatureTwo[i]
            if (!paramTwo.isAssignableFrom(paramOne)) {
                return false
            }
        }

        return true
    }

    override fun <VM : VectorViewModel<S>, S : VectorState> create(
        vmClass: KClass<out VM>,
        stateClass: KClass<out S>,
        viewModelOwner: ViewModelOwner,
        savedStateRegistryOwner: SavedStateRegistryOwner,
        stateStoreContext: CoroutineContext
    ): ViewModelProvider.Factory {

        val stateFactory: VectorStateFactory = RealStateFactory()
        // Using constructors.first() instead of primaryConstructor because it doesn't play well with ProGuard/R8
        val constructor = vmClass.constructors.first()

        val parameters = vmClass.java.constructors.first().parameterTypes

        return VectorSavedStateViewModelFactory(savedStateRegistryOwner, null) { _, handle ->
            val initialState = stateFactory.createInitialState(vmClass, stateClass, handle, viewModelOwner)

            @Suppress("UNCHECKED_CAST")
            when {
                parameters.compareSignature(supportedSignatures[0]) -> {
                    vmClass.java.newInstance()
                }
                parameters.compareSignature(supportedSignatures[1]) -> {
                    vmClass.java.instance(initialState) as VM
                }
                parameters.compareSignature(supportedSignatures[2]) -> {
                    constructor.call(initialState, stateStoreContext)
                }
                parameters.compareSignature(supportedSignatures[3]) -> {
                    constructor.call(initialState, handle)
                }
                parameters.compareSignature(supportedSignatures[4]) -> {
                    constructor.call(initialState, stateStoreContext, handle)
                }
                parameters.compareSignature(supportedSignatures[5]) -> {
                    constructor.call(initialState, stateStoreContext, androidLogger(vmClass.java.simpleName))
                }
                else -> throw NoSuitableViewModelConstructorException()
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
        stateStoreContext: CoroutineContext
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
