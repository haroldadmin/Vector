package com.haroldadmin.vector.state

import com.haroldadmin.vector.VectorState
import kotlinx.coroutines.CoroutineScope

internal interface Action<S : VectorState>

internal inline class SetStateAction<S : VectorState>(val reducer: suspend S.() -> S) :
    Action<S>

internal inline class GetStateAction<S : VectorState>(val block: suspend (S) -> Unit) :
    Action<S>

interface StateProcessor<S : VectorState> : CoroutineScope {
    fun offerSetAction(action: suspend S.() -> S)
    fun offerGetAction(action: suspend (S) -> Unit)
    fun clearProcessor()
}
