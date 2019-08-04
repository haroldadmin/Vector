package com.haroldadmin.sampleapp.addEditEntity

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.haroldadmin.sampleapp.R
import com.haroldadmin.sampleapp.databinding.FragmentAddEntityBinding
import com.haroldadmin.sampleapp.repository.EntitiesRepository
import com.haroldadmin.sampleapp.utils.debouncedTextChanges
import com.haroldadmin.sampleapp.utils.provider
import com.haroldadmin.vector.VectorFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AddEditEntityFragment : VectorFragment() {

    private lateinit var binding: FragmentAddEntityBinding
    private val safeArgs by navArgs<AddEditEntityFragmentArgs>()
    private val viewModel by viewModels<AddEditEntityViewModel> {
        val repository = EntitiesRepository(provider().database.countingEntityQueries)
        AddEditEntityViewModelFactory(this, null, safeArgs.entityId, repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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

            fragmentScope.launch {
                name.debouncedTextChanges(200)
                    .collect { name ->
                        Log.d("AEEF", "Debounced name: $name")
                        viewModel.setName(name.toString())
                    }
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentScope.launch {
            viewModel.state.collect { state ->
                renderState(state, this@AddEditEntityFragment::renderer)
            }
        }
    }

    private fun renderer(state: AddEditEntityState) {

        when (state) {
            is AddEditEntityState.AddEntity -> {
                with(binding) {
                    count.text = state.count.toString()
                }

                if (state.isSaved) {
                    Snackbar
                        .make(binding.root, R.string.entitySavedMessage, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }

            is AddEditEntityState.EditEntity -> {
                with(binding) {
                    count.text = state.count.toString()
                    if (name.text.toString() != state.name) {
                        name.setText(state.name)
                    }
                }

                if (state.isSaved) {
                    Snackbar
                        .make(binding.root, R.string.entitySavedMessage, Snackbar.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }
}