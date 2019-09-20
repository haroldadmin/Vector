package com.haroldadmin.vector

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStoreOwner
import kotlin.reflect.KClass

/**
 * Creates an initial state for a ViewModel using either the [VectorViewModelFactory] or using
 * the default constructor
 */
internal interface VectorStateFactory {

    fun <S : VectorState> createInitialState(
        vmClass: KClass<*>,
        stateClass: KClass<out S>,
        handle: SavedStateHandle,
        owner: ViewModelOwner
    ): S
}

internal class RealStateFactory : VectorStateFactory {

    override fun <S : VectorState> createInitialState(
        vmClass: KClass<*>,
        stateClass: KClass<out S>,
        handle: SavedStateHandle,
        owner: ViewModelOwner
    ): S {
        return getStateFromVectorVMFactory(vmClass, handle, owner)
            ?: getDefaultStateFromConstructor(stateClass) //
            ?: throw UnInstantiableStateClassException(stateClass.java.simpleName)
    }

    /**
     * Checks if the ViewModel implements a [VectorViewModelFactory] in its companion object, and if so, uses it
     * to create the initial state if the factory implements the corresponding function. If any of these conditions are not met,
     * returns null
     */
    private fun <S : VectorState> getStateFromVectorVMFactory(
        vmClass: KClass<*>,
        handle: SavedStateHandle,
        owner: ViewModelOwner
    ): S? {

        val factoryClass = try {
            vmClass.factoryCompanion()
        } catch (ex: DoesNotImplementVectorVMFactoryException) {
            null
        }

        return factoryClass?.let { clazz ->
            try {
                @Suppress("UNCHECKED_CAST")
                clazz.getMethod("initialState", SavedStateHandle::class.java, ViewModelOwner::class.java)
                    .invoke(factoryClass.instance(), handle, owner) as S?
            } catch (ex: NoSuchMethodException) {
                // Look for JvmStatic method
                @Suppress("UNCHECKED_CAST")
                clazz.getMethod("initialState", SavedStateHandle::class.java, ViewModelStoreOwner::class.java)
                    .invoke(null, handle, owner) as S?
            }
        }
    }

    /**
     * Creates the initial state using the default constructor of the state class.
     */
    private fun <S : VectorState> getDefaultStateFromConstructor(stateClass: KClass<out S>): S? {
        return stateClass.let {
            try {
                // Use Java reflection version for new instance creation as it is faster
                @Suppress("UNCHECKED_CAST")
                stateClass.java.newInstance()
            } catch (e: NoSuchMethodException) {
                null
            } catch (e: InstantiationException) {
                null
            }
        }
    }
}

internal class UnInstantiableStateClassException(
    className: String
) : IllegalArgumentException("$className could not be instantiated without a VectorViewModelFactory")