package com.haroldadmin.vector.loggers

import com.haroldadmin.vector.Vector
import com.haroldadmin.vector.loggers.Logger
import java.lang.StringBuilder

internal class StringLogger : Logger {

    private var logBuilder = StringBuilder()

    override val tag: String = "StringLogger"

    override fun log(message: String) {
        if (Vector.enableLogging) {
            logBuilder.append("$tag: $message").append("\n")
        }
    }

    fun getLog() = logBuilder.toString()
}