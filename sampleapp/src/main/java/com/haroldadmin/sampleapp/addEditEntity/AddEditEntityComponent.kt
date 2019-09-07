package com.haroldadmin.sampleapp.addEditEntity

import dagger.Binds
import dagger.Module
import dagger.Subcomponent
import dagger.android.AndroidInjector
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Subcomponent
interface AddEditEntityComponent: AndroidInjector<AddEditEntityFragment> {
    @Subcomponent.Factory
    interface Factory: AndroidInjector.Factory<AddEditEntityFragment>
}

@Module(subcomponents = [AddEditEntityComponent::class])
interface AddEditEntityModule {
    @Binds
    @IntoMap
    @ClassKey(AddEditEntityFragment::class)
    fun bindAddEditEntityFragment(factory: AddEditEntityComponent.Factory): AndroidInjector.Factory<*>
}