package com.haroldadmin.vector.state

import com.haroldadmin.vector.VectorState

/**
 * A class which can hold current state as well as handle actions to be performed on it.
 *
 * @param stateHolder The delegate to handle [StateHolder] functions
 * @param stateProcessor The delegate to handle [StateProcessor] functions
 */
abstract class StateStore<S : VectorState>(
    internal open val stateHolder: StateHolder<S>,
    internal open val stateProcessor: StateProcessor<S>
) : StateHolder<S> by stateHolder, StateProcessor<S> by stateProcessor {

    /**
     * Clear any resources held by this state store.
     * Implementations should also forward the call to [stateHolder] and [stateProcessor]
     */
    abstract fun clear()
}