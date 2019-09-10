package com.haroldadmin.sampleapp

import android.content.Context
import com.haroldadmin.sampleapp.addEditEntity.AddEditEntityFragment
import com.haroldadmin.sampleapp.addEditEntity.AddEditEntityModule
import com.haroldadmin.sampleapp.entities.EntitiesFragment
import com.haroldadmin.sampleapp.entities.EntitiesModule
import com.squareup.inject.assisted.dagger2.AssistedModule
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.android.AndroidInjectionModule

@Component(modules = [AppModule::class, EntitiesModule::class, AddEditEntityModule::class, RepositoryModule::class, AndroidInjectionModule::class])
interface AppComponent {

    fun inject(application: EntityCounter)
    fun inject(entitiesFragment: EntitiesFragment)
    fun inject(addEditEntityFragment: AddEditEntityFragment)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }
}

@AssistedModule
@Module(includes = [AssistedInject_AppModule::class])
object AppModule