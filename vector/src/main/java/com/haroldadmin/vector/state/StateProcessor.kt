package com.haroldadmin.vector.state

import com.haroldadmin.vector.VectorState

/**
 * An entity that manages any action on state.
 *
 * @param S The state type implementing [VectorState]
 *
 * A [reducer] is be processed before any existing [action] in the queue
 * A [action] is given the latest state value as it's parameter
 */
interface StateProcessor<S : VectorState> {

    /**
     * Offer a [reducer] to this processor. This action will be processed as soon as
     * possible, before all existing [action] waiting in the queue, if any.
     *
     * @param reducer The action to be offered
     */
    fun offerSetAction(reducer: reducer<S>)

    /**
     * Offer a [action] to this processor. The state parameter supplied to this action
     * shall be the latest state value at the time of processing this action.
     *
     * These actions are treated as side effects. When such an action is received, a separate coroutine is launched
     * to process it. This means that when there are multiple such actions waiting in the queue, they will be launched
     * in order, but their completion depends on how long it takes to process them. They will be processed in the
     * coroutine context of their state processor.
     */
    fun offerGetAction(action: action<S>)

    /**
     * Cleanup any resources held by this processor.
     */
    fun clearProcessor()
}

internal typealias reducer<S> = suspend S.() -> S

internal typealias action<S> = suspend (S) -> Unit