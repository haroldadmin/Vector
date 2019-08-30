package com.haroldadmin.sampleapp

import android.content.Context
import dagger.BindsInstance
import dagger.Component

@Component(modules = [RepositoryModule::class])
interface AppComponent {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}