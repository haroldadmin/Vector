package com.haroldadmin.vector.loggers

import com.haroldadmin.vector.Vector

internal class SystemOutLogger : Logger {
    override val tag: String = "Vector"

    override fun log(message: String) {
        if (Vector.enableLogging) {
            println("$tag: $message")
        }
    }
}

fun systemOutLogger(): Logger = SystemOutLogger()