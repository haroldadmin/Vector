package com.haroldadmin.vector

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.androidLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * Lazy delegate for creating a [VectorViewModel] from a Fragment
 *
 * Creates and returns the requested [VectorViewModel] automatically using reflection.
 * The returned ViewModel is scoped to this Fragment.
 *
 * @param VM The type of the [VectorViewModel] being requested
 * @param S The type of State class bound to the requested ViewModel
 * @param stateStoreContext The [CoroutineContext] to be used in the ViewModel's state store
 * @param logger The [Logger] to be used in the ViewModel
 *
 */
inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> Fragment.fragmentViewModel(
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job(),
    logger: Logger = androidLogger()
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            VM::class,
            S::class,
            this,
            fragmentViewModelOwner(),
            stateStoreContext,
            logger
        )
    }
}

/**
 * Lazy delegate for creating a [VectorViewModel] from a Fragment using the given producer lambda
 *
 * Creates and returns the requested [VectorViewModel] automatically using reflection.
 * The returned ViewModel is scoped to this Fragment.
 *
 * @param VM The type of the [VectorViewModel] being requested
 * @param S The type of State class bound to the requested ViewModel
 * @param viewModelCreator The lambda which creates and returns the requested ViewModel
 *
 */
inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> Fragment.fragmentViewModel(
    noinline viewModelCreator: (initialState: S, handle: SavedStateHandle) -> VM
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            vmClass = VM::class,
            stateClass = S::class,
            viewModelOwner = fragmentViewModelOwner(),
            savedStateRegistryOwner = this,
            viewModelProducer = viewModelCreator
        )
    }
}

/**
 * Lazy delegate for creating an activity scoped [VectorViewModel] from a Fragment. Creates and returns the requested
 * [VectorViewModel] automatically using reflection. The returned ViewModel is scoped to this Fragment's parent activity.
 *
 * @param VM The type of the [VectorViewModel] being requested
 * @param S The type of State class bound to the requested ViewModel
 * @param stateStoreContext The [CoroutineContext] to be used in the ViewModel's state store
 * @param logger The [Logger] to be used in the ViewModel
 *
 */
inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> Fragment.activityViewModel(
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job(),
    logger: Logger = androidLogger()
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            vmClass = VM::class,
            stateClass = S::class,
            savedStateRegistryOwner = requireActivity(),
            viewModelOwner = activityViewModelOwner(),
            stateStoreContext = stateStoreContext,
            logger = logger
        )
    }
}

/**
 * Lazy delegate for creating a parent activity scoped [VectorViewModel] from a Fragment using the given producer lambda
 *
 * Creates and returns the requested [VectorViewModel] automatically using reflection.
 * The returned ViewModel is scoped to this Fragment's parent activity.
 *
 * @param VM The type of the [VectorViewModel] being requested
 * @param S The type of State class bound to the requested ViewModel
 * @param viewModelCreator The lambda which creates and returns the requested ViewModel
 *
 */
inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> Fragment.activityViewModel(
    noinline viewModelCreator: (initialState: S, handle: SavedStateHandle) -> VM
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            vmClass = VM::class,
            stateClass = S::class,
            savedStateRegistryOwner = requireActivity(),
            viewModelOwner = activityViewModelOwner(),
            viewModelProducer = viewModelCreator
        )
    }
}

/**
 * Lazy delegate for creating a [VectorViewModel] from an Activity
 *
 * Creates and returns the requested [VectorViewModel] automatically using reflection.
 * The returned ViewModel is scoped to this activity
 *
 * @param VM The type of the [VectorViewModel] being requested
 * @param S The type of State class bound to the requested ViewModel
 * @param stateStoreContext The [CoroutineContext] to be used in the ViewModel's state store
 * @param logger The [Logger] to be used in the ViewModel
 *
 */
inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> ComponentActivity.viewModel(
    stateStoreContext: CoroutineContext = Dispatchers.Default + Job(),
    logger: Logger = androidLogger()
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            vmClass = VM::class,
            stateClass = S::class,
            savedStateRegistryOwner = this,
            viewModelOwner = activityViewModelOwner(),
            stateStoreContext = stateStoreContext,
            logger = logger
        )
    }
}

/**
 * Lazy delegate for creating a [VectorViewModel] from an Activity using the given producer lambda
 *
 * Creates and returns the requested [VectorViewModel] automatically using reflection.
 * The returned ViewModel is scoped to this activity.
 *
 * @param VM The type of the [VectorViewModel] being requested
 * @param S The type of State class bound to the requested ViewModel
 * @param viewModelCreator The lambda which creates and returns the requested ViewModel
 *
 */
inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> ComponentActivity.viewModel(
    noinline viewModelCreator: (initialState: S, handle: SavedStateHandle) -> VM
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            vmClass = VM::class,
            stateClass = S::class,
            savedStateRegistryOwner = this,
            viewModelOwner = activityViewModelOwner(),
            viewModelProducer = viewModelCreator
        )
    }
}
