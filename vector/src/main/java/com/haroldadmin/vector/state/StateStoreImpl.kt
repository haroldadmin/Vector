package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState

/**
 * The default implementation of [StateStore]
 */
internal class StateStoreImpl<S : VectorState> (
    holder: StateHolder<S>,
    processor: StateProcessor<S>,
    private val logger: Logger
) : StateStore<S>(holder, processor) {

    override fun clear() {
        logger.log("Clearing State Store")
        stateHolder.clearHolder()
        stateProcessor.clearProcessor()
    }
}