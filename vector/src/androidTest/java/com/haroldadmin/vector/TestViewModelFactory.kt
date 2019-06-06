package com.haroldadmin.vector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

internal class TestViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            TestViewModel::class.java -> {
                val initialState = TestState()
                TestViewModel(
                    initialState
                ) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel requested")
        }
    }
}