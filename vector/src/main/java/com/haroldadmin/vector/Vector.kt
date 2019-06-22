package com.haroldadmin.vector

import android.util.Log

object Vector {

    private const val TAG = "Vector"
    private var loggingEnabled = false

    fun enableLogging() { loggingEnabled = true }

    fun disableLogging() { loggingEnabled = false }

    internal fun log(message: String) {
        if (loggingEnabled) Log.d(TAG, message)
    }
}