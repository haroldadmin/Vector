package com.haroldadmin.vector

inline fun <S : VectorState, A : VectorAction> withState(
    viewModel: VectorViewModel<S, A>,
    crossinline block: (S) -> Unit
) {
    block(viewModel.currentState)
}

suspend inline fun <S : VectorState, A : VectorAction> withStateSuspend(
    viewModel: VectorViewModel<S, A>,
    crossinline block: suspend (S) -> Unit
) {
    block(viewModel.currentState)
}
