package com.haroldadmin.sampleapp

import android.app.Application
import com.haroldadmin.sampleapp.utils.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class EntityCounter : Application(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.Main
    private val applicationScope = CoroutineScope(coroutineContext)

    lateinit var provider: Provider

    override fun onCreate() {
        super.onCreate()
        provider = Provider(this, applicationScope)
    }
}