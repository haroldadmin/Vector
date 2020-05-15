package com.haroldadmin.hellovector

import android.os.Parcelable
import com.haroldadmin.vector.VectorState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class HelloState(
    val message: String = uninitializedMessage
) : VectorState, Parcelable {
    companion object {
        const val uninitializedMessage = "..."
        const val loadingMessage = "Loading..."
        const val helloMessage = "Hello, World!"
    }
}