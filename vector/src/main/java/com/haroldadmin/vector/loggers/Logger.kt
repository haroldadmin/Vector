package com.haroldadmin.vector.loggers

/**
 * Used to write debug logs.
 *
 * Implementations of this interface can be used to provide different output sources for log
 * statements, such as Android's built in Logging class, or STDOUT.
 */
interface Logger {

    /**
     * A name tag associated with this logger for identification and filtering
     */
    val tag: String

    /**
     * Logs the given message to the associated output
     *
     * @param message The message to be logged
     */
    fun log(message: String)
}