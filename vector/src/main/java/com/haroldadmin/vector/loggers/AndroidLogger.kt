package com.haroldadmin.vector.loggers

import android.util.Log
import com.haroldadmin.vector.Vector
import com.haroldadmin.vector.loggers.Logger.*

/**
 * An implementation of [Logger] which writes out to the standard Android Log.
 *
 * Logs are only written if logging is enabled.
 */
internal class AndroidLogger(override val tag: String) : Logger {

    override fun log(message: String, level: Level) {
        if (!Vector.enableLogging) {
            return
        }
        when (level) {
            Level.DEBUG -> Log.d(tag, message)
            Level.VERBOSE -> Log.v(tag, message)
        }
    }
}

/**
 * A utility function to create instances of [AndroidLogger]
 */
fun androidLogger(tag: String): Logger = AndroidLogger(tag)