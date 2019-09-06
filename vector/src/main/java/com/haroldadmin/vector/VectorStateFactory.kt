package com.haroldadmin.vector

import androidx.lifecycle.SavedStateHandle

interface VectorStateFactory {

    fun <S: VectorState> createInitialState(
        vmClass: Class<*>,
        stateClass: Class<*>,
        handle: SavedStateHandle
    ): S

}

class RealStateFactory: VectorStateFactory {
    override fun <S : VectorState> createInitialState(
        vmClass: Class<*>,
        stateClass: Class<*>,
        handle: SavedStateHandle
    ): S {
        return getStateFromVectorVMFactory<S>(vmClass, handle)
            ?: getDefaultStateFromConstructor(stateClass)
            ?: throw UnInstantiableStateClassException(stateClass.simpleName)
    }

    private fun <S: VectorState> getStateFromVectorVMFactory(vmClass: Class<*>, handle: SavedStateHandle): S? {

        val factoryClass = try {
            vmClass.factoryCompanion()
        } catch (ex: DoesNotImplementVectorVMFactoryException) {
            null
        }

        return factoryClass?.let { clazz ->
            try {
                @Suppress("UNCHECKED_CAST")
                clazz.getMethod("initialState", SavedStateHandle::class.java)
                    .invoke(factoryClass.instance(), handle) as S?
            } catch (ex: NoSuchMethodException) {
                // Look for JvmStatic method
                @Suppress("UNCHECKED_CAST")
                clazz.getMethod("initialState", SavedStateHandle::class.java)
                    .invoke(null, handle) as S?
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