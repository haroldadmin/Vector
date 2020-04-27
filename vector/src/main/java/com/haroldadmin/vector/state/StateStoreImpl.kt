package com.haroldadmin.vector.state

import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.VectorState
import com.haroldadmin.vector.loggers.logv

/**
 * The default implementation of [StateStore]
 */
internal class StateStoreImpl<S : VectorState> (
    holder: StateHolder<S>,
    processor: StateProcessor<S>,
    private val logger: Logger
) : StateStore<S>(holder, processor) {

    override fun clear() {
        logger.logv { "Clearing State Store" }
        // TODO Clear processor before clearing the state holder to prevent the processor from sending updates
        //  to a closed state channel
        stateHolder.clearHolder()
        stateProcessor.clearProcessor()
    }
}