package com.haroldadmin.sampleapp.addEntity

import com.haroldadmin.vector.VectorState

data class AddEntityState(
    val temporaryEntity: TemporaryEntity = TemporaryEntity(),
    val isSaving: Boolean = false,
    val isSaved: Boolean = false
): VectorState