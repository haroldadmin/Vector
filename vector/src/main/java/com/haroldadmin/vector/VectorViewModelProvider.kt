package com.haroldadmin.vector

import android.os.Bundle
import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import java.lang.NoSuchMethodException
import java.lang.InstantiationException

@RestrictTo(RestrictTo.Scope.LIBRARY)
object VectorViewModelProvider {

    fun <VM: VectorViewModel<S>, S: VectorState> get(
        vmClass: Class<out VM>,
        stateClass: Class<out S>,
        savedStateRegistryOwner: SavedStateRegistryOwner,
        viewModelOwner: ViewModelOwner,
        defaultArgs: Bundle?,
        stateFactory: VectorStateFactory = RealStateFactory()
    ): VM {

        val factory = VectorSavedStateViewModelFactory(savedStateRegistryOwner, defaultArgs) { modelClass, handle ->
            val initialState = stateFactory.createInitialState<S>(modelClass, stateClass, handle)
            createViewModel<VM, S>(modelClass, stateClass, initialState)
        }

        return when (viewModelOwner) {
            is ActivityViewModelOwner -> ViewModelProvider(viewModelOwner.activity, factory)
            is FragmentViewModelOwner -> ViewModelProvider(viewModelOwner.fragment, factory)
        }.get(vmClass)
    }


    @RestrictTo(RestrictTo.Scope.LIBRARY)
    fun <VM : VectorViewModel<S>, S : VectorState> createViewModel(
        vmClass: Class<*>,
        stateClass: Class<*>,
        initialState: S
    ): VM {
        return vmClass.factoryCompanion().let { factoryClass ->
            try {
                // Invoke companion factory method
                @Suppress("UNCHECKED_CAST")
                factoryClass
                    .getMethod("create", stateClass)
                    .invoke(factoryClass.instance(), initialState) as VM
            } catch (ex: NoSuchMethodException){
                @Suppress("UNCHECKED_CAST")
                factoryClass
                    .getMethod("create", stateClass)
                    .invoke(null, initialState) as VM
            } catch (ex: NoSuchMethodException) {
                // Try instantiating with the constructor directly
                @Suppress("UNCHECKED_CAST")
                vmClass.instance(initialState) as VM
            } catch (ex: InstantiationException) {
                throw UnInstantiableViewModelException(vmClass.simpleName)
            }
        }
    }
}

class UnInstantiableViewModelException(className: String)
    : IllegalArgumentException("$className can not be instantiated with a proper companion factory method")

class DoesNotImplementVectorVMFactoryException :
    Exception("This class's companion object does not implement a VectorViewModel Factory")
