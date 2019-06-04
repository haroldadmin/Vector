package com.haroldadmin.vector

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * A Fragment which has a convenient fragmentScope property
 * to easily launch coroutines in it.
 *
 * Users are not required to extend their fragments from this class.
 */
abstract class VectorFragment : Fragment(), CoroutineScope {

    private val job by lazy { Job() }
    override val coroutineContext: CoroutineContext by lazy { Dispatchers.Main + job }

    protected val fragmentScope by lazy { CoroutineScope(coroutineContext) }

    protected abstract fun renderState()

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}