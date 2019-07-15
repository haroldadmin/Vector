package com.haroldadmin.vector.loggers

import android.util.Log
import com.haroldadmin.vector.Logger

internal class AndroidLogger : Logger {

    override val tag: String = "Vector"

    override fun log(message: String) {
        Log.d(tag, message)
    }
}

fun androidLogger(): Logger = AndroidLogger()