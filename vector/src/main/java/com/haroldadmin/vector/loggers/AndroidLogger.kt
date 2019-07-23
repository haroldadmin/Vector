package com.haroldadmin.vector.loggers

import android.util.Log
import com.haroldadmin.vector.Vector

internal class AndroidLogger : Logger {

    override val tag: String = "Vector"

    override fun log(message: String) {
        if (Vector.enableLogging) {
            Log.d(tag, message)
        }
    }
}

fun androidLogger(): Logger = AndroidLogger()