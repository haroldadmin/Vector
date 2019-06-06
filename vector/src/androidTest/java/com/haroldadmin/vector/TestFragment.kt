package com.haroldadmin.vector

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

internal class TestFragment(vmFactory: TestViewModelFactory) : VectorFragment() {

    // Exposed publicly for testing
    val viewModel by viewModels<TestViewModel> { vmFactory }

    // Exposed publicly for testing
    lateinit var state: TestState

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("TestFragment", "Subscribing to state")
        viewModel.state.observe(this, Observer { renderState() })
    }

    override fun renderState() = withState(viewModel) { vmState ->
        Log.d("TestFragment", "Rendering new state: $vmState")
        this.state = vmState
    }
}