package com.haroldadmin.vector

object Vector {

    private const val TAG = "Vector"
    private var logger: Logger? = null

    fun enableLogging(logger: Logger) { this.logger = logger }

    fun disableLogging() { logger = null }

    internal fun log(message: String) {
        logger?.log(message)
    }
}