package com.haroldadmin.sampleapp.addEntity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.haroldadmin.sampleapp.R
import com.haroldadmin.sampleapp.databinding.FragmentAddEntityBinding
import com.haroldadmin.sampleapp.repository.EntitiesRepository
import com.haroldadmin.sampleapp.utils.afterTextChanged
import com.haroldadmin.sampleapp.utils.provider
import com.haroldadmin.vector.VectorFragment
import com.haroldadmin.vector.withState

private const val KEY_STATE = "state"

class AddEntityFragment : VectorFragment() {

    private lateinit var binding: FragmentAddEntityBinding
    private lateinit var viewModel: AddEntityViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentAddEntityBinding.inflate(inflater, container, false)

        binding.apply {
            btIncrease.setOnClickListener {
                viewModel.incrementCount()
            }

            btDecrease.setOnClickListener {
                viewModel.decrementCount()
            }

            saveEntity.setOnClickListener {
                viewModel.saveEntity()
            }

            name.afterTextChanged { newText ->
                viewModel.setName(newText.toString())
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val persistedState: AddEntityState? = savedInstanceState?.getParcelable(KEY_STATE)
        initializeViewModel(persistedState)
        initializeView(persistedState)

        viewModel.state.observe(viewLifecycleOwner, Observer {
            renderState()
        })
    }

    override fun renderState() = withState(viewModel) { state ->
        if (state.isSaved) {
            Snackbar.make(binding.root, R.string.entitySavedMessage, Snackbar.LENGTH_SHORT).show()
        }

        binding.count.text = state.count.toString()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        withState(viewModel) { state ->
            outState.putParcelable(KEY_STATE, state)
        }
    }

    private fun initializeViewModel(persistedState: AddEntityState?) {
        val repository = EntitiesRepository(provider().database.countingEntityQueries)
        val factory = AddEntityViewModelFactory(repository, persistedState)

        viewModel = ViewModelProviders.of(this, factory).get(AddEntityViewModel::class.java)
    }

    private fun initializeView(persistedState: AddEntityState?) {
        persistedState ?: return
        binding.name.setText(persistedState.name)
        binding.count.text = persistedState.count.toString()
    }
}