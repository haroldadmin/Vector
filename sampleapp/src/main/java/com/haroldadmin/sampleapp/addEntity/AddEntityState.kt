package com.haroldadmin.sampleapp.addEntity

import android.os.Parcelable
import com.haroldadmin.vector.VectorState
import kotlinx.android.parcel.Parcelize

@Parcelize
data class AddEntityState(
    val temporaryEntity: TemporaryEntity = TemporaryEntity(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
) : VectorState, Parcelable