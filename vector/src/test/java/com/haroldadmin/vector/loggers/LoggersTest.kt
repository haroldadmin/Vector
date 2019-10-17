package com.haroldadmin.vector.loggers

import com.haroldadmin.vector.Vector
import org.junit.Test

internal class LoggersTest {
    private val logger = StringLogger()

    @Test
    fun `Logging a string when logging disabled should produce no log`() {
        Vector.enableLogging = false

        val message = "Goodbye, world!"
        logger.log(message)

        assert(logger.getLog().isBlank())
    }

    @Test
    fun `Logging a string produces an output log`() {
        Vector.enableLogging = true

        val message = "Hello, world!"
        logger.log(message)

        assert(logger.getLog().contains(message))
    }

    @Test
    fun `Logging level test`() {
        Vector.enableLogging = true
        val message = "Hello, world!"
        logger.logd { message }
        assert(logger.getLog().contains(Regex("""^D/${logger.tag}.+""")))

        logger.clear()

        logger.logv { message }
        assert(logger.getLog().contains(Regex("""^V/${logger.tag}.+""")))
    }
}