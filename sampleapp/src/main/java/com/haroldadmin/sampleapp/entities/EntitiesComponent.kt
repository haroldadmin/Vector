package com.haroldadmin.sampleapp.entities

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Subcomponent
interface EntitiesComponent: AndroidInjector<EntitiesFragment> {
    @Subcomponent.Factory
    interface Factory: AndroidInjector.Factory<EntitiesFragment>
}

@Module(subcomponents = [EntitiesComponent::class])
interface EntitiesModule {
    @Binds
    @IntoMap
    @ClassKey(EntitiesFragment::class)
    fun bindEntitiesFragment(factory: EntitiesComponent.Factory): AndroidInjector.Factory<*>
}