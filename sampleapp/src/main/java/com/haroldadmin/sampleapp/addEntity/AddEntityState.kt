package com.haroldadmin.sampleapp.addEntity

import android.os.Parcelable
import com.haroldadmin.vector.VectorState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddEntityState(
    val name: String = "",
    val count: Long = 0,
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
): VectorState, Parcelable