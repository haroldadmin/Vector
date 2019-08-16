package com.haroldadmin.vector

import androidx.fragment.app.Fragment
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect

/**
 * A Fragment which has a convenient fragmentScope property
 * to easily launch coroutines in it.
 *
 * Users are not required to extend their fragments from this class.
 */
abstract class VectorFragment : Fragment() {

    protected open val fragmentScope by lazy { CoroutineScope(Dispatchers.Main + Job()) }

    @Deprecated(message = """
        Use the parameterized renderState method instead.
        This method WILL be removed before the 1.0 release.
        """)
    // TODO Remove this method before the 1.0 release
    protected open fun renderState() = Unit

    /**
     * Renders the UI based on the given [state] parameter using the [renderer] block
     * If your fragment is tied to a [VectorViewModel] then consider using the overloaded version
     * of the method which takes in a viewModel as an input parameter
     */
    protected inline fun <reified S: VectorState> renderState(state: S, renderer: (S) -> Unit) {
        renderer(state)
    }

    /**
     * Renders the UI based on emitted state updates from the given [viewModel] using the [renderer]
     * block.
     */
    protected inline fun <S: VectorState> renderState(viewModel: VectorViewModel<S>, crossinline renderer: (S) -> Unit) {
        fragmentScope.launch {
            viewModel.state.collect { state ->
                renderer(state)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentScope.cancel()
    }
}
