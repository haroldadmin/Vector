package com.haroldadmin.vector

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.haroldadmin.vector.loggers.androidLogger
import com.haroldadmin.vector.loggers.logd
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

/**
 * A Fragment which has a convenient fragmentScope property
 * to easily launch coroutines in it.
 *
 */
abstract class VectorFragment : Fragment() {

    /**
     * A [CoroutineScope] associated with the lifecycle of this fragment. The scope is cancelled when
     * [onDestroy] of this Fragment has been called.
     */
    protected open val fragmentScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.Main + Job()) }

    /**
     * A [CoroutineScope] associated with the view-lifecycle of this fragment. The scope is cancelled
     * when [onDestroyView] of this Fragment has been called, and created when [onCreateView] is called.
     *
     * It delegates to the lifecycle-runtime library's [androidx.lifecycle.lifecycleScope] extension
     */
    protected open val viewScope: CoroutineScope
        get() = viewLifecycleOwner.lifecycleScope

    protected open val logger by lazy { androidLogger(this::class.java.simpleName) }

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
     * block. **MUST BE CALLED IN onViewCreated()**
     *
     * Launches a coroutine in [viewScope] which collects state updates from the given [viewModel] and calls
     * the [renderer] method on it. Since the renderer method might contain references to views, and also since
     * this method should only run while the view of the fragment is available, it is launched in the [viewScope]
     * rather than the [fragmentScope]. As such, it must be called in [onViewCreated] after the [viewScope] is
     * available and the view can be updated. The collection of state updates automatically stops when
     * [onDestroyView] is called.
     *
     * @param viewModel The ViewModel whose [VectorViewModel.state] flow is used to receive state updates and
     * render the UI
     * @param renderer The method which updates the UI
     */
    protected inline fun <S : VectorState> renderState(viewModel: VectorViewModel<S>, crossinline renderer: (S) -> Unit) {
        viewScope.launch {
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
