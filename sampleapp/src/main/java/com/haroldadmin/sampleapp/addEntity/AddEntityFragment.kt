package com.haroldadmin.sampleapp.addEntity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.haroldadmin.sampleapp.R
import com.haroldadmin.sampleapp.databinding.FragmentAddEntityBinding
import com.haroldadmin.sampleapp.utils.provider
import com.haroldadmin.vector.VectorFragment
import com.haroldadmin.vector.withState

class AddEntityFragment: VectorFragment() {

    private lateinit var binding: FragmentAddEntityBinding
    private val viewModel by viewModels<AddEntityViewModel> {
        AddEntityViewModelFactory(provider().entitiesRepository, null)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddEntityBinding.inflate(inflater, container, false)

        binding.apply {
            btIncrease.setOnClickListener { viewModel.incrementCount() }
            btDecrease.setOnClickListener { viewModel.decrementCount() }
            saveEntity.setOnClickListener { viewModel.saveEntity(name.text.toString()) }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.state.observe(viewLifecycleOwner, Observer {
            renderState()
        })
    }

    override fun renderState() = withState(viewModel) { state ->
        if (state.isSaved) {
            Snackbar.make(binding.root, R.string.entitySavedMessage, Snackbar.LENGTH_SHORT).show()
        }
        
        binding.count.text = state.temporaryEntity.count.toString()
    }

}