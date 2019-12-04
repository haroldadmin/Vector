package com.haroldadmin.vector.extensions

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.runBlocking

/**
 * Takes in an [action] to be run, completes only after that action has completed and returns the result value.
 * The action is expected to call [complete] on the supplied [CompletableDeferred] with the an result value.
 *
 * Multiple invocations of complete have no effect, only the first call affects the result value.
 */
internal fun <T> awaitCompletion(action: suspend CompletableDeferred<T>.() -> Unit): T = runBlocking {
    val completable = CompletableDeferred<T>()
    action(completable)
    val result = completable.await()
    result
}
