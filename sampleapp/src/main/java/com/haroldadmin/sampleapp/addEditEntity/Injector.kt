package com.haroldadmin.sampleapp.addEditEntity

import com.haroldadmin.sampleapp.EntityCounter

fun AddEditEntityFragment.inject() {
    (requireActivity().application as EntityCounter)
        .appComponent
        .inject(this)
}