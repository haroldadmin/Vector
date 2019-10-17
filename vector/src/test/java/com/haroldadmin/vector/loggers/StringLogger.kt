package com.haroldadmin.vector.loggers

import com.haroldadmin.vector.Vector
import com.haroldadmin.vector.loggers.Logger.*
import java.lang.StringBuilder

internal class StringLogger : Logger {

    private var logBuilder = StringBuilder()

    override val tag: String = "StringLogger"

    override fun log(message: String, level: Level) {
        if (!Vector.enableLogging) {
            return
        }
        when (level) {
            Level.DEBUG -> logBuilder.append("D/$tag: $message").append("\n")
            Level.VERBOSE -> logBuilder.append("V/$tag: $message").append("\n")
        }
    }

    fun clear() = logBuilder.clear()

    fun getLog() = logBuilder.toString()
}