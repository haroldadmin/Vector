package com.haroldadmin.sampleapp.addEntity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.haroldadmin.sampleapp.R
import com.haroldadmin.sampleapp.databinding.FragmentAddEntityBinding
import com.haroldadmin.sampleapp.utils.provider
import com.haroldadmin.vector.VectorFragment
import com.haroldadmin.vector.withState

private const val KEY_STATE = "state"
private const val KEY_NAME = "name"

class AddEntityFragment : VectorFragment() {

    private lateinit var binding: FragmentAddEntityBinding
    private lateinit var viewModel: AddEntityViewModel

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
        initializeViewModel(savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner, Observer {
            renderState()
        })
    }

    override fun renderState() = withState(viewModel) { state ->
        Log.d("AddEntityFragment", "State: $state")
        if (state.isSaved) {
            Snackbar.make(binding.root, R.string.entitySavedMessage, Snackbar.LENGTH_SHORT).show()
        }

        binding.count.text = state.temporaryEntity.count.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        withState(viewModel) { state ->
            outState.apply {
                putString(KEY_NAME, binding.name.text.toString())
                putParcelable(KEY_STATE, state)
            }
        }
    }

    private fun initializeViewModel(savedInstanceState: Bundle?) {
        val initialState = if (savedInstanceState != null) {
            val state = savedInstanceState.getParcelable(KEY_STATE) ?: AddEntityState()
            val name = savedInstanceState.getString(KEY_NAME) ?: ""
            val tempEntity = state.temporaryEntity.copy(name = name)
            state.copy(temporaryEntity = tempEntity)
        } else {
            AddEntityState()
        }

        val factory = AddEntityViewModelFactory(provider().entitiesRepository, initialState)

        viewModel = ViewModelProviders.of(this, factory).get(AddEntityViewModel::class.java)
    }
}