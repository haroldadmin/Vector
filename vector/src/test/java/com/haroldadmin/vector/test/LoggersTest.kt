package com.haroldadmin.vector.test

import com.haroldadmin.vector.Logger
import com.haroldadmin.vector.Vector
import org.junit.Test
import java.lang.StringBuilder

private class StringLogger: Logger {

    private var logBuilder = StringBuilder()

    override val tag: String = "StringLogger"

    override fun log(message: String) {
        logBuilder.append("$tag: $message").append("\n")
    }

    fun getLog() = logBuilder.toString()
}

internal class LoggersTest {

    @Test
    fun logTest() {
        val logger = StringLogger()
        Vector.enableLogging(logger)
        Vector.log("42")

        assert(logger.getLog().startsWith(logger.tag))
        assert(logger.getLog().contains("42"))
    }
}