package com.haroldadmin.sampleapp

import android.app.Application
import com.haroldadmin.sampleapp.utils.Provider
import com.haroldadmin.vector.Vector

class EntityCounter : Application() {

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        Vector.enableLogging = false
        appComponent = DaggerAppComponent
            .factory()
            .create(this)
    }
}