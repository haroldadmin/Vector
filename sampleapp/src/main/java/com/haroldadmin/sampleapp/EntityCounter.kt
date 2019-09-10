package com.haroldadmin.sampleapp

import android.app.Application
import com.haroldadmin.vector.Vector
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

class EntityCounter : Application(), HasAndroidInjector {

    @Inject lateinit var androidInjector: DispatchingAndroidInjector<Any>

    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        Vector.enableLogging = true
        appComponent = DaggerAppComponent
            .factory()
            .create(this)
            .also { comp -> comp.inject(this) }
    }

    override fun androidInjector(): AndroidInjector<Any> {
        return androidInjector
    }
}