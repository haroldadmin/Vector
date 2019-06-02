package com.haroldadmin.vector.fragmentTest

import com.haroldadmin.vector.VectorFragment
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

internal class TestFragment : VectorFragment() {

    private fun counter() = produce {
        var counter = 0
        while (true) {
            send(counter++)
            delay(100)
        }
    }

    fun count(): Job = fragmentScope.launch {
        counter().consumeEach {
            println("Count = $it")
            delay(100)
        }
    }

    override fun invalidate() {
    }
}