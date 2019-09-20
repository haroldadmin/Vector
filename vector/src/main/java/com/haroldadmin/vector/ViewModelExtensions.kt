package com.haroldadmin.vector

import androidx.activity.ComponentActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle
import com.haroldadmin.vector.loggers.Logger
import com.haroldadmin.vector.loggers.androidLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

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

inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> Fragment.activityViewModel(
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

inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> Fragment.activityViewModel(
    noinline producer: (initialState: S, handle: SavedStateHandle) -> VM
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            vmClass = VM::class,
            stateClass = S::class,
            savedStateRegistryOwner = this,
            viewModelOwner = activityViewModelOwner(),
            viewModelProducer = producer
        )
    }
}

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

inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> ComponentActivity.viewModel(
    noinline producer: (initialState: S, handle: SavedStateHandle) -> VM
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            vmClass = VM::class,
            stateClass = S::class,
            savedStateRegistryOwner = this,
            viewModelOwner = activityViewModelOwner(),
            viewModelProducer = producer
        )
    }
}
