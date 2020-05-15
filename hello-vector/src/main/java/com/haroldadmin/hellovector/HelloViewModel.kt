package com.haroldadmin.hellovector

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.haroldadmin.vector.SavedStateVectorViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class HelloViewModel(
    initialState: HelloState,
    coroutineContext: CoroutineContext = Dispatchers.Default + Job(),
    savedStateHandle: SavedStateHandle
) : SavedStateVectorViewModel<HelloState>(initialState, coroutineContext, savedStateHandle) {

    companion object {
        const val defaultDelay = 1000L
    }

    fun getMessage(delayDuration: Long = defaultDelay) = viewModelScope.launch {
        setStateAndPersist { copy(message = HelloState.loadingMessage) }
        delay(delayDuration)
        setStateAndPersist { copy(message = HelloState.helloMessage) }
    }
}