package com.haroldadmin.sampleapp.addEditEntity

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.snackbar.Snackbar
import com.haroldadmin.sampleapp.R
import com.haroldadmin.sampleapp.databinding.FragmentAddEntityBinding
import com.haroldadmin.sampleapp.utils.debouncedTextChanges
import com.haroldadmin.vector.VectorFragment
import com.haroldadmin.vector.fragmentViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

class AddEditEntityFragment : VectorFragment() {

    @Inject lateinit var viewModelFactory: AddEditEntityViewModel.Factory
    private lateinit var binding: FragmentAddEntityBinding

    private val viewModel: AddEditEntityViewModel by fragmentViewModel()

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
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
                        viewModel.setName(name.toString())
                    }
            }
        }

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        renderState(viewModel) { state ->
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
}