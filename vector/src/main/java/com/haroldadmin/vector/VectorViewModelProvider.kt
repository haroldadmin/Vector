package com.haroldadmin.vector

import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.androidLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import java.lang.NoSuchMethodException
import kotlin.coroutines.CoroutineContext

/**
 * A class which is responsible for creating ViewModel instances
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
object VectorViewModelProvider {

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun <VM : VectorViewModel<S>, S : VectorState> get(
        vmClass: Class<out VM>,
        stateClass: Class<out S>,
        savedStateRegistryOwner: SavedStateRegistryOwner,
        viewModelOwner: ViewModelOwner,
        defaultArgs: Bundle?,
        stateFactory: VectorStateFactory = RealStateFactory()
    ): VM {

        val factory = VectorSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) { modelClass, handle ->
            val initialState = stateFactory.createInitialState<S>(modelClass, stateClass, handle, viewModelOwner)
            createViewModel<VM, S>(modelClass, stateClass, initialState, viewModelOwner, handle)
        }

        return when (viewModelOwner) {
            is ActivityViewModelOwner -> ViewModelProvider(viewModelOwner.activity, factory)
            is FragmentViewModelOwner -> ViewModelProvider(viewModelOwner.fragment, factory)
        }.get(vmClass)
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun <VM : VectorViewModel<S>, S : VectorState> get(
        vmClass: Class<out VM>,
        stateClass: Class<out S>,
        savedStateRegistryOwner: SavedStateRegistryOwner,
        viewModelOwner: ViewModelOwner,
        viewModelProducer: (initialState: S, handle: SavedStateHandle) -> VM,
        stateFactory: VectorStateFactory = RealStateFactory(),
        defaultArgs: Bundle? = null
    ): VM {
        val factory = VectorSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) { modelClass, handle ->
            val initialState = stateFactory.createInitialState<S>(modelClass, stateClass, handle, viewModelOwner)
            viewModelProducer(initialState, handle)
        }

        return when (viewModelOwner) {
            is ActivityViewModelOwner -> ViewModelProvider(viewModelOwner.activity, factory)
            is FragmentViewModelOwner -> ViewModelProvider(viewModelOwner.fragment, factory)
        }.get(vmClass)
    }

    @Suppress("UNCHECKED_CAST")
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun <VM : VectorViewModel<S>, S : VectorState> createViewModel(
        vmClass: Class<*>,
        stateClass: Class<*>,
        initialState: S,
        owner: ViewModelOwner,
        handle: SavedStateHandle,
        stateStoreContext: CoroutineContext = Dispatchers.Default + Job(),
        logger: Logger = androidLogger()
    ): VM {
        return try {
            vmClass.factoryCompanion().let { factoryClass ->
                try {
                    // Invoke companion factory method
                    factoryClass
                        .getMethod("create", stateClass, ViewModelOwner::class.java, SavedStateHandle::class.java)
                        .invoke(factoryClass.instance(), initialState, owner, handle) as VM
                } catch (ex: NoSuchMethodException) {
                    factoryClass
                        .getMethod("create", stateClass, ViewModelOwner::class.java, SavedStateHandle::class.java)
                        .invoke(null, initialState, owner, handle) as VM
                }
            }
        } catch (ex: DoesNotImplementVectorVMFactoryException) {
            val constructor = vmClass.kotlin.constructors.first() // Using Kotlin constructor here to access parameters size
            when (constructor.parameters.size) {
                1 -> vmClass.instance(initialState) as VM
                2 -> constructor.call(initialState, handle) as VM
                3 -> constructor.call(initialState, stateStoreContext, logger) as VM
                4 -> constructor.call(initialState, stateStoreContext, logger, handle) as VM
                else -> throw UnInstantiableViewModelException(vmClass.simpleName)
            }
        }
    }
}

internal class UnInstantiableViewModelException(className: String) :
    IllegalArgumentException("$className can not be instantiated with a proper companion factory method")

internal class DoesNotImplementVectorVMFactoryException :
    Exception("This class's companion object does not implement a VectorViewModel Factory")
