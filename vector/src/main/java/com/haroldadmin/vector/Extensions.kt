package com.haroldadmin.vector

import kotlinx.coroutines.channels.ConflatedBroadcastChannel

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

/**
 * A convenience function to update the value present in the Receiving [ConflatedBroadcastChannel]
 *
 * Takes in a function to calculate a new value based on the current value in the channel,
 * and then sets the new value to the channel.
 */
internal inline fun <T> ConflatedBroadcastChannel<T>.compute(crossinline newValueProvider: (T) -> T): Boolean {

    if (this.isClosedForSend) return false

    val newValue = newValueProvider.invoke(this.value)
    this.offer(newValue)
    return true
}