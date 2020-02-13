package com.haroldadmin.hellovector

import android.os.Parcelable
import com.haroldadmin.vector.VectorState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HelloState(
    val message: String = loadingMessage
) : VectorState, Parcelable {
    companion object {
        const val loadingMessage = "Loading..."
        const val helloMessage = "Hello, World!"
    }
}