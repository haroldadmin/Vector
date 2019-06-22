package com.haroldadmin.sampleapp

import android.app.Application
import com.haroldadmin.sampleapp.utils.Provider
import com.haroldadmin.vector.Vector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

class EntityCounter : Application(), CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Main

    lateinit var provider: Provider

    override fun onCreate() {
        super.onCreate()
        Vector.enableLogging()
        provider = Provider(context = this, coroutineScope = this)
    }
}