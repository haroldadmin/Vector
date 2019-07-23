package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState
import kotlin.coroutines.CoroutineContext

internal object StateProcessorFactory {

    fun <S: VectorState> create(
        stateHolder: StateHolder<S>,
        logger: Logger,
        coroutineContext: CoroutineContext
    ): StateProcessor<S> {
        return StateProcessorActor(stateHolder, logger, coroutineContext)
    }
}