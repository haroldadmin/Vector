package com.haroldadmin.vector.loggers

interface Logger {

    val tag: String

    fun log(message: String)
}