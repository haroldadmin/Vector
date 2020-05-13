package com.haroldadmin.vector

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.collect

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
 *
 * Warning: This WILL cause your app to crash if you create your ViewModels without initial state
 * and fail to provide it later, before calling this function.
 */
inline fun <S : VectorState> withState(
    viewModel: VectorViewModel<S>,
    crossinline block: (S) -> Unit
) {
    block(viewModel.currentState)
}

/**
 * Renders the UI based on the given [state] parameter using the [renderer] block. If your fragment is tied to a
 * [VectorViewModel] then consider using the overloaded version of the method which takes in a viewModel as an
 * input parameter.
 *
 * @param state The state instance using which the UI should be rendered
 * @param renderer The method which updates the UI state
 */
@Suppress("unused")
inline fun <S : VectorState> Fragment.renderState(state: S, renderer: (S) -> Unit) {
    renderer(state)
}

/**
 * Renders the UI based on emitted state updates from the given [viewModel] using the [renderer]
 * block.
 *
 * Launches a coroutine in the view's lifecycle scope which collects state updates from the given
 * [viewModel] and calls the [renderer] method on it. The renderer method interacts with the Fragment's views, and
 * therefore must only be called within the lifecycle of the view. As such, use it in or after [Fragment.onCreateView].
 *
 * The [renderer] parameter is a suspending function
 * It can be used to safely run coroutines which affect the UI.
 *
 * @param viewModel The ViewModel whose [VectorViewModel.state] flow is used to receive state updates and
 * render the UI
 * @param renderer The method which updates the UI
 */
inline fun <S : VectorState> Fragment.renderState(
    viewModel: VectorViewModel<S>,
    crossinline renderer: suspend (S) -> Unit
) {

    viewLifecycleOwner.lifecycleScope.launchWhenCreated {
        viewModel.state.collect { state ->
            renderer(state)
        }
    }
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
