package com.haroldadmin.sampleapp.utils

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.haroldadmin.sampleapp.EntityCounter
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*

fun Fragment.provider() = ((requireActivity().application) as EntityCounter).provider

fun View.hide() {
    this.visibility = View.GONE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun EditText.debouncedTextChanges(time: Long = 200): Flow<CharSequence> {
    val channel = Channel<CharSequence>(capacity = Channel.UNLIMITED)

    val textWatcher = object : TextWatcher {
        override fun afterTextChanged(text: Editable) {}
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
            channel.offer(text)
        }
    }

    this.addTextChangedListener(textWatcher)

    return channel
        .consumeAsFlow()
        .debounce(time)
        .onCompletion {
            this@debouncedTextChanges.removeTextChangedListener(textWatcher)
            channel.close()
        }
}