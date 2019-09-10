package com.haroldadmin.vector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.SavedStateHandle

inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> Fragment.fragmentViewModel(
    defaultArgs: Bundle? = null
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            VM::class.java,
            S::class.java,
            this,
            fragmentViewModelOwner(),
            defaultArgs
        )
    }
}

inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> Fragment.fragmentViewModel(
    noinline viewModelCreator: (initialState: S, handle: SavedStateHandle) -> VM
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            VM::class.java,
            S::class.java,
            this,
            fragmentViewModelOwner(),
            viewModelCreator
        )
    }
}

inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> Fragment.activityViewModel(): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            vmClass = VM::class.java,
            stateClass = S::class.java,
            savedStateRegistryOwner = this,
            viewModelOwner = activityViewModelOwner(),
            defaultArgs = null
        )
    }
}

inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> Fragment.activityViewModel(
    noinline producer: (initialState: S, handle: SavedStateHandle) -> VM
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            vmClass = VM::class.java,
            stateClass = S::class.java,
            savedStateRegistryOwner = this,
            viewModelOwner = activityViewModelOwner(),
            viewModelProducer = producer
        )
    }
}

inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> AppCompatActivity.viewModel(): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            vmClass = VM::class.java,
            stateClass = S::class.java,
            savedStateRegistryOwner = this,
            viewModelOwner = activityViewModelOwner(),
            defaultArgs = null
        )
    }
}

inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> AppCompatActivity.viewModel(
    noinline producer: (initialState: S, handle: SavedStateHandle) -> VM
): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            vmClass = VM::class.java,
            stateClass = S::class.java,
            savedStateRegistryOwner = this,
            viewModelOwner = activityViewModelOwner(),
            viewModelProducer = producer
        )
    }
}