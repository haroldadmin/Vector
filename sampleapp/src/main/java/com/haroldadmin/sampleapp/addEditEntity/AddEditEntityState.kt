package com.haroldadmin.sampleapp.addEditEntity

import android.os.Parcelable
import com.haroldadmin.vector.VectorState
import kotlinx.android.parcel.Parcelize
import java.util.*

sealed class AddEditEntityState : VectorState, Parcelable {

    @Parcelize
    data class AddEntity(
        val id: String = UUID.randomUUID().toString(),
        val name: String = "",
        val count: Long = 0,
        val isSaved: Boolean = false
    ) : AddEditEntityState()

    @Parcelize
    data class EditEntity(
        val id: String,
        val name: String = "",
        val count: Long = 0,
        val isSaved: Boolean = false
    ) : AddEditEntityState()
}