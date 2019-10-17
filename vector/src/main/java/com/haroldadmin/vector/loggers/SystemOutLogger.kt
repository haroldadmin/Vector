package com.haroldadmin.vector.loggers

import com.haroldadmin.vector.Vector
import com.haroldadmin.vector.loggers.Logger.*

/**
 * An implementation of [Logger] which writes logs to [System.out]
 *
 * Logs are only written if logging is enabled.
 */
internal class SystemOutLogger(override val tag: String) : Logger {

    override fun log(message: String, level: Level) {
        if (!Vector.enableLogging) {
            return
        }
        when (level) {
            Level.DEBUG -> println("D/$tag: $message")
            Level.VERBOSE -> println("V/$tag: $message")
        }
    }
}

fun systemOutLogger(tag: String = "Vector"): Logger = SystemOutLogger(tag)