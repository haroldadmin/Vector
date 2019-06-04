package com.haroldadmin.sampleapp.addEntity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.haroldadmin.sampleapp.repository.EntitiesRepository

class AddEntityViewModelFactory(val repository: EntitiesRepository, private val initialState: AddEntityState?): ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass == AddEntityViewModel::class.java) {
            return AddEntityViewModel(initialState, repository) as T
        } else {
            throw IllegalArgumentException("Unknown ViewModel $modelClass requested")
        }
    }

}