package com.haroldadmin.vector

interface Logger {

    val tag: String

    fun log(message: String)
}