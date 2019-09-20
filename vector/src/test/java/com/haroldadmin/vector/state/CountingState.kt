package com.haroldadmin.vector.state

import android.os.Parcelable
import com.haroldadmin.vector.VectorState
import kotlinx.android.parcel.Parcelize

@Parcelize
internal data class CountingState(val count: Int = 0) : VectorState, Parcelable
