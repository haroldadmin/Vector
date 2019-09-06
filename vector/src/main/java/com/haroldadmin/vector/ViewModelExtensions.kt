package com.haroldadmin.vector

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

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

inline fun <reified VM : VectorViewModel<S>, reified S : VectorState> Fragment.activityViewModel(): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            VM::class.java,
            S::class.java,
            this,
            activityViewModelOwner(),
            null
        )
    }
}

inline fun <reified VM: VectorViewModel<S>, reified S: VectorState> AppCompatActivity.viewModel(): vectorLazy<VM> {
    return vectorLazy {
        VectorViewModelProvider.get(
            VM::class.java,
            S::class.java,
            this,
            activityViewModelOwner(),
            null
        )
    }
}
