package com.haroldadmin.hellovector

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.haroldadmin.vector.VectorFragment
import com.haroldadmin.vector.fragmentViewModel
import kotlinx.android.synthetic.main.fragment_message.view.messageButton
import kotlinx.android.synthetic.main.fragment_message.view.messageTextView
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class HelloFragment : VectorFragment() {

    private val viewModel: HelloViewModel by fragmentViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_message, container, false)

        root.messageButton.setOnClickListener {
            viewModel.getMessage()
        }

        renderState(viewModel) { state ->
            root.messageTextView.text = state.message
        }

        return root
    }
}