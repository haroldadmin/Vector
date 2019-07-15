package com.haroldadmin.sampleapp

import android.app.Application
import com.haroldadmin.sampleapp.utils.Provider
import com.haroldadmin.vector.Vector
import com.haroldadmin.vector.loggers.androidLogger

class EntityCounter : Application() {

    lateinit var provider: Provider

    override fun onCreate() {
        super.onCreate()
        Vector.enableLogging(androidLogger())
        provider = Provider(context = this)
    }
}