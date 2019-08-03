package com.haroldadmin.sampleapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.haroldadmin.sampleapp.EntityCounter

fun Fragment.provider() = ((requireActivity().application) as EntityCounter).provider

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun EditText.afterTextChanged(block: (Editable) -> Unit) {
    this.addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(newText: Editable) {
            block(newText)
        }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
    })
}