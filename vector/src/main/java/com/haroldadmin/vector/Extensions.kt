package com.haroldadmin.vector

import com.haroldadmin.vector.viewModel.VectorViewModel

/**
 * A convenience function to access current state and execute an action on it.
 *
 * @param S The type of the current state
 * @param block The action to be performed using the current state
 *
 * Example:
 *
 * class MyViewModel(): VectorViewModel<MyState>()
 *
 * class MyFragment(): VectorFragment {
 *      onViewCreated(...) {
 *         withState(viewModel) { state ->
 *              if (state.isPremiumUser) {
 *                  premiumFeature.enable()
 *              }
 *         }
 *      }
 * }
 *
 * Note: The state provided to the block is not guaranteed to be the latest state, because there
 * might be other state mutation blocks in the State Store's queue
 */
inline fun <S : VectorState> withState(
    viewModel: VectorViewModel<S>,
    crossinline block: (S) -> Unit
) {
    block(viewModel.currentState)
}

