package com.haroldadmin.vector

import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.androidLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * A Subclass of [VectorViewModel] that has access to a [SavedStateHandle] to easily
 * persist state properties in case of process death
 */
abstract class SavedStateVectorViewModel<S : VectorState>(
    initialState: S?,
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job(),
    logger: Logger = androidLogger(),
    protected val savedStateHandle: SavedStateHandle
) : VectorViewModel<S>(initialState, stateStoreContext, logger) {

    companion object {
        const val KEY_SAVED_STATE = "vector:saved-state"
    }

}