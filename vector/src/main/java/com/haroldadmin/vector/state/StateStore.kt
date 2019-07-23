package com.haroldadmin.vector.state

import com.haroldadmin.vector.VectorState

abstract class StateStore<S : VectorState>(
    protected open val stateHolder: StateHolder<S>,
    protected open val stateProcessor: StateProcessor<S>
) : StateHolder<S> by stateHolder, StateProcessor<S> by stateProcessor {

    abstract fun clear()
}