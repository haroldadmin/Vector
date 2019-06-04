package com.haroldadmin.vector

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class TestViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when (modelClass) {
            TestViewModel::class.java -> TestViewModel(
                TestState(
                    count = 0
                )
            ) as T
            else -> throw IllegalArgumentException("Unknown ViewModel requested")
        }
    }
}