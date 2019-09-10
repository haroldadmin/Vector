package com.haroldadmin.sampleapp.entities

import com.haroldadmin.sampleapp.EntityCounter

fun EntitiesFragment.inject() {
    (requireActivity().application as EntityCounter)
        .appComponent
        .inject(this)
}