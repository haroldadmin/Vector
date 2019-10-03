package com.haroldadmin.vector

/**
 * Singleton object to configure the library
 *
 */
object Vector {

    /**
     * Enables/Disables logging based on its value
     *
     * If true, then all loggers shall write their logs.
     * If false, then no loggers shall write any logs.
     *
     * Can be changed at runtime.
     */
    var enableLogging: Boolean = false
}