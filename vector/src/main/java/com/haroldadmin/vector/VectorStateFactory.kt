package com.haroldadmin.vector

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModelStoreOwner

interface VectorStateFactory {

    fun <S: VectorState> createInitialState(
        vmClass: Class<*>,
        stateClass: Class<*>,
        handle: SavedStateHandle,
        owner: ViewModelOwner
    ): S

}

class RealStateFactory: VectorStateFactory {
    override fun <S : VectorState> createInitialState(
        vmClass: Class<*>,
        stateClass: Class<*>,
        handle: SavedStateHandle,
        owner: ViewModelOwner
    ): S {
        return getStateFromVectorVMFactory<S>(vmClass, handle, owner)
            ?: getDefaultStateFromConstructor(stateClass)
            ?: throw UnInstantiableStateClassException(stateClass.simpleName)
    }

    private fun <S: VectorState> getStateFromVectorVMFactory(
        vmClass: Class<*>,
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

    private fun <S: VectorState> getDefaultStateFromConstructor(stateClass: Class<*>): S? {
        return stateClass.let {
            try {
                @Suppress("UNCHECKED_CAST")
                stateClass.newInstance() as S?
            } catch (e: NoSuchMethodException) {
                null
            } catch (e: InstantiationException) {
                null
            }
        }
    }
}

class UnInstantiableStateClassException(
    className: String
): IllegalArgumentException("$className could not be instantiated without a VectorViewModelFactory")