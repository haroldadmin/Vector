package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState

object StateHolderFactory {

    fun <S: VectorState> create (initialState: S, logger: Logger): StateHolder<S> {
        return StateHolderImpl(initialState, logger)
    }
}