package com.haroldadmin.sampleapp.entities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.haroldadmin.sampleapp.repository.EntitiesRepository

class EntitiesViewModelFactory(private val entitiesRepository: EntitiesRepository, private val initialState: EntitiesState? = null) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass == EntitiesViewModel::class.java) {
            EntitiesViewModel(repository = entitiesRepository, initState = initialState) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel $modelClass requested")
        }
    }
}