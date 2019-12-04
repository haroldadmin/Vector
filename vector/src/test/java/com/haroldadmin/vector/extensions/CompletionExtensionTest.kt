package com.haroldadmin.vector.extensions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class CompletionExtensionTest {

    @Test
    fun `should finish executing only after complete has been called`() = runBlocking {
        var isCompleted = false
        awaitCompletion<Unit> {
            launch(Dispatchers.Default) {
                launch(Dispatchers.Default) {
                    isCompleted = true
                    complete(Unit)
                }
            }
        }
        assert(isCompleted)
    }
}