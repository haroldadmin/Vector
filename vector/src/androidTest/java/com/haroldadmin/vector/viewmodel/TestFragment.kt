package com.haroldadmin.vector.viewmodel

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.haroldadmin.vector.VectorFragment
import com.haroldadmin.vector.withState
import kotlinx.coroutines.launch

class TestFragment : VectorFragment() {

    private val viewModel: TestViewModel by lazy {
        ViewModelProviders.of(this, TestViewModelFactory()).get(TestViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fragmentScope.launch { viewModel.add() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner, Observer { invalidate() })
    }

    override fun invalidate() = withState(viewModel) { state ->
        Log.d(javaClass.simpleName, "Current state -> $state")
    }
}