package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState

/**
 * A factory class to create instances of [StateHolder]
 */
internal object StateHolderFactory {

    /**
     * Creates and returns a [StateHolder] with a null initial state
     * A new non null state should be set as soon as possible, or before accessing the state.
     *
     * @param logger The logger to be used by the state holder for debug logs
     */
    fun <S: VectorState> create(logger: Logger): StateHolder<S> {
        return StateHolderImpl(null, logger)
    }

    /**
     * Creates and returns a [StateHolder].
     *
     * @param initialState The initial state to be passed to the state holder
     * @param logger The logger to be used by the state holder for debug logs
     *
     * @return A class that implements the state holder interface
     */
    fun <S : VectorState> create(initialState: S, logger: Logger): StateHolder<S> {
        return StateHolderImpl(initialState, logger)
    }
}