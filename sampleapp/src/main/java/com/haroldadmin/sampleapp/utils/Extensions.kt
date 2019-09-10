package com.haroldadmin.sampleapp.utils

import android.view.View
import androidx.fragment.app.Fragment
import com.haroldadmin.sampleapp.EntityCounter

fun Fragment.provider() = ((requireActivity().application) as EntityCounter).provider

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}