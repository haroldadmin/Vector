package com.haroldadmin.vector

import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

/**
 * A Fragment which has a convenient fragmentScope property
 * to easily launch coroutines in it.
 *
 */
abstract class VectorFragment : Fragment() {

    /**
     * A [kotlinx.coroutines.CoroutineScope] associated with the lifecycle of this fragment. The scope is cancelled when
     * [onDestroy] of this Fragment has been called.
     */
    protected open val fragmentScope by lazy { CoroutineScope(Dispatchers.Main + Job()) }

    /**
     * Renders the UI based on the given [state] parameter using the [renderer] block. If your fragment is tied to a
     * [VectorViewModel] then consider using the overloaded version of the method which takes in a viewModel as an
     * input parameter.
     *
     * @param state The state instance using which the UI should be rendered
     * @param renderer The method which updates the UI state
     *
     */
    protected inline fun <reified S : VectorState> renderState(state: S, renderer: (S) -> Unit) {
        renderer(state)
    }

    /**
     * Renders the UI based on emitted state updates from the given [viewModel] using the [renderer]
     * block.
     *
     * @param viewModel The ViewModel whose [VectorViewModel.state] flow is used to receive state updates and
     * render the UI
     * @param renderer The method which updates the UI
     */
    protected inline fun <S : VectorState> renderState(viewModel: VectorViewModel<S>, crossinline renderer: (S) -> Unit) {
        fragmentScope.launch {
            viewModel.state.collect { state ->
                renderer(state)
            }
        }
    }

    /**
     * Cancels the [fragmentScope]
     */
    override fun onDestroy() {
        super.onDestroy()
        fragmentScope.cancel()
    }
}
