package com.haroldadmin.vector.loggers

import com.haroldadmin.vector.Vector

/**
 * An implementation of [Logger] which writes logs to [System.out]
 *
 * Logs are only written if logging is enabled.
 */
internal class SystemOutLogger(override val tag: String) : Logger {

    override fun log(message: String) {
        if (Vector.enableLogging) {
            println("$tag: $message")
        }
    }
}

fun systemOutLogger(tag: String = "Vector"): Logger = SystemOutLogger(tag)