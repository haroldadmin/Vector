package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState
import kotlin.coroutines.CoroutineContext

internal object StateStoreFactory {

    fun <S : VectorState> create(
        initialState: S,
        logger: Logger,
        coroutineContext: CoroutineContext
    ): StateStore<S> {
        val stateHolder = StateHolderFactory.create(initialState, logger)
        val stateProcessor = StateProcessorFactory.create(stateHolder, logger, coroutineContext)
        return create(stateHolder, stateProcessor, logger)
    }

    fun <S : VectorState> create(
        stateHolder: StateHolder<S>,
        stateProcessor: StateProcessor<S>,
        logger: Logger
    ): StateStore<S> {
        return StateStoreImpl(stateHolder, stateProcessor, logger)
    }

    fun <S : VectorState> create(
        initialState: S,
        logger: Logger,
        stateHolderFactory: StateHolderFactory,
        stateProcessorFactory: StateProcessorFactory,
        coroutineContext: CoroutineContext
    ): StateStore<S> {
        val holder = stateHolderFactory.create(initialState, logger)
        val processor = stateProcessorFactory.create(holder, logger, coroutineContext)
        return create(holder, processor, logger)
    }
}
