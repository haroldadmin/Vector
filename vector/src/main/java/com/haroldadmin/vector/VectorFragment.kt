package com.haroldadmin.vector

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.haroldadmin.vector.loggers.androidLogger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * A Fragment which has a convenient fragmentScope property
 * to easily launch coroutines in it.
 */
abstract class VectorFragment : Fragment() {

    /**
     * A [CoroutineScope] associated with the lifecycle of this fragment. The scope is cancelled when
     * [onDestroy] of this Fragment has been called.
     */
    @Deprecated(
        message = "Use the AndroidX provided lifecycleScope extension instead",
        replaceWith = ReplaceWith(
            "lifecycleScope",
            "androidx.lifecycle.lifecycleScope"
        )
    )
    protected open val fragmentScope: CoroutineScope
        get() = lifecycleScope

    /**
     * A [CoroutineScope] associated with the view-lifecycle of this fragment. The scope is cancelled
     * when [onDestroyView] of this Fragment has been called, and created when [onCreateView] is called.
     *
     * This is deprecated, and simply delegates to the lifecycle library's [androidx.lifecycle.lifecycleScope] property
     */
    @Deprecated(
        message = "Use the AndroidX provided lifecycle scope extension instead",
        replaceWith = ReplaceWith(
            "viewLifecycleOwner.lifecycleScope",
            "androidx.lifecycle.lifecycleScope"
        )
    )
    protected open val viewScope: CoroutineScope
        get() = viewLifecycleOwner.lifecycleScope

    /**
     * Renders the UI based on the given [state] parameter using the [renderer] block. If your fragment is tied to a
     * [VectorViewModel] then consider using the overloaded version of the method which takes in a viewModel as an
     * input parameter.
     *
     * @param state The state instance using which the UI should be rendered
     * @param renderer The method which updates the UI state
     */
    protected inline fun <reified S : VectorState> renderState(state: S, renderer: (S) -> Unit) {
        renderer(state)
    }

    /**
     * Renders the UI based on emitted state updates from the given [viewModel] using the [renderer]
     * block.
     *
     * Launches a coroutine in the view's lifecycle scope which collects state updates from the given
     * [viewModel] and calls the [renderer] method on it. The renderer method interacts with the Fragment's views, and
     * therefore must only be called within the lifecycle of the view. As such, use it in or after [onCreateView].
     *
     * The [renderer] parameter is a suspending function with a CoroutineScope of the Fragment's view lifecycle.
     * It can be used to safely run coroutines which affect the UI.
     *
     * @param viewModel The ViewModel whose [VectorViewModel.state] flow is used to receive state updates and
     * render the UI
     * @param renderer The method which updates the UI
     */
    protected inline fun <S : VectorState> renderState(
        viewModel: VectorViewModel<S>,
        crossinline renderer: suspend CoroutineScope.(S) -> Unit
    ) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.state.collect { state ->
                renderer(state)
            }
        }
    }

    protected open val logger by lazy { androidLogger(this::class.java.simpleName) }
}
