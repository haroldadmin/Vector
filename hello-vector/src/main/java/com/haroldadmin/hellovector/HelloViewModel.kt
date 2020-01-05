package com.haroldadmin.hellovector

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.haroldadmin.vector.SavedStateVectorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

@ExperimentalCoroutinesApi
class HelloViewModel(
    initialState: HelloState,
    coroutineContext: CoroutineContext = Dispatchers.Default + Job(),
    savedStateHandle: SavedStateHandle
) : SavedStateVectorViewModel<HelloState>(initialState, coroutineContext, savedStateHandle) {

    fun getMessage(delayDuration: Long = 1000) = viewModelScope.launch {
        setStateAndPersist { copy(message = "Loading...") }
        delay(delayDuration)
        setStateAndPersist { copy(message = "Hello, World!") }
    }

}