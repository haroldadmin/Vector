package com.haroldadmin.vector.state

import com.haroldadmin.vector.VectorState

/**
 * A class which can hold current state as well as handle actions to be performed on it.
 *
 * @param stateHolder The delegate to handle [StateHolder] functions
 * @param stateProcessor The delegate to handle [StateProcessor] functions
 */
abstract class StateStore<S : VectorState>(
    protected open val stateHolder: StateHolder<S>,
    protected open val stateProcessor: StateProcessor<S>
) : StateHolder<S> by stateHolder, StateProcessor<S> by stateProcessor {

    /**
     * Can be used to set initial state if the state holder supplied to this StateStore was created
     * without one.
     *
     * The initial state should not be set using the state processor, because it expects
     * that at least one state value should be present in the state holder when processing set-state blocks
     */
    fun setInitialState(state: S) {
        stateHolder.stateObservable.offer(state)
    }

    /**
     * Clear any resources held by this state store.
     * Implementations should also forward the call to [stateHolder] and [stateProcessor]
     */
    abstract fun clear()
}