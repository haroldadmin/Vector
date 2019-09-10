package com.haroldadmin.sampleapp.addEntity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TemporaryEntity(
    val count: Long = 0,
    val name: String = ""
) : Parcelable