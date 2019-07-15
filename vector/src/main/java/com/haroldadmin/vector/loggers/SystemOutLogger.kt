package com.haroldadmin.vector.loggers

import com.haroldadmin.vector.Logger

internal class SystemOutLogger : Logger {
    override val tag: String = "Vector"

    override fun log(message: String) = println("$tag: $message")
}

fun systemOutLogger(): Logger = SystemOutLogger()