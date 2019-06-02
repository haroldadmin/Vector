package com.haroldadmin.vector

/**
 * A convenience function to access current state and execute an action on it.
 *
 * @param S The type of the current state
 * @param A The type parameter for accessing the view model
 * @param block The action to take on the current state
 *
 * Example:
 *
 * class MyViewModel(): VectorViewModel<MyState, MyAction>()
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
 */
inline fun <S : VectorState, A : VectorAction> withState(
    viewModel: VectorViewModel<S, A>,
    crossinline block: (S) -> Unit
) {
    block(viewModel.currentState)
}


/**
 * Suspending version of [withState] to allow for a suspending function
 * to be passed in as an action.
 */
suspend inline fun <S : VectorState, A : VectorAction> withStateSuspend(
    viewModel: VectorViewModel<S, A>,
    crossinline block: suspend (S) -> Unit
) {
    block(viewModel.currentState)
}
