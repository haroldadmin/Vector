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

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    protected val fragmentScope = CoroutineScope(coroutineContext)

    protected abstract fun invalidate()

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}