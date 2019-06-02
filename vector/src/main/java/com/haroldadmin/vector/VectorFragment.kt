package com.haroldadmin.vector

import androidx.fragment.app.Fragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class VectorFragment : Fragment(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job

    protected val fragmentScope = CoroutineScope(coroutineContext)

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}