package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState
import kotlin.coroutines.CoroutineContext

/**
 * A Factory which produces instances of [StateProcessor]
 */
internal object StateProcessorFactory {

    /**
     * Create and return an instance of [StateProcessor]
     *
     * @param S The state type to be associated with this processor
     * @param logger A logger to be supplied to the state processor
     * @param coroutineContext The context of execution of the state processor
     *
     * @return A class implementing StateProcessor
     */
    fun <S : VectorState> create(
        stateHolder: StateHolder<S>,
        logger: Logger,
        coroutineContext: CoroutineContext
    ): StateProcessor<S> {
        return SelectBasedStateProcessor(
            shouldStartImmediately = true,
            stateHolder = stateHolder,
            logger = logger,
            coroutineContext = coroutineContext
        )
    }
}