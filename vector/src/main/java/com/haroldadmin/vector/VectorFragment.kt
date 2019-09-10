package com.haroldadmin.vector

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel

/**
 * A Fragment which has a convenient fragmentScope property
 * to easily launch coroutines in it.
 *
 * Users are not required to extend their fragments from this class.
 */
abstract class VectorFragment : Fragment() {

    protected open val fragmentScope by lazy { CoroutineScope(Dispatchers.Main + Job()) }

    protected abstract fun renderState()

    override fun onDestroy() {
        super.onDestroy()
        fragmentScope.cancel()
    }
}
