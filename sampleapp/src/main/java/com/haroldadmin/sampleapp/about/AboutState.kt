package com.haroldadmin.sampleapp.about

import com.haroldadmin.sampleapp.BuildConfig
import com.haroldadmin.vector.VectorState

data class AboutState(
    val appVersion: String = BuildConfig.VERSION_NAME,
    val libraryVersion: String = com.haroldadmin.vector.BuildConfig.VERSION_NAME
) : VectorState