package com.haroldadmin.sampleapp.entities

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.haroldadmin.sampleapp.databinding.FragmentEntitiesBinding
import com.haroldadmin.sampleapp.repository.EntitiesRepository
import com.haroldadmin.sampleapp.utils.hide
import com.haroldadmin.sampleapp.utils.provider
import com.haroldadmin.sampleapp.utils.show
import com.haroldadmin.vector.VectorFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class EntitiesFragment : VectorFragment() {

    private lateinit var binding: FragmentEntitiesBinding

    private val viewModel by viewModels<EntitiesViewModel> {
        val repository = EntitiesRepository(provider().database.countingEntityQueries)
        EntitiesViewModelFactory(repository, EntitiesState(
            entities = null,
            isLoading = true
        ))
    }

    private val entitiesAdapter = EntitiesAdapter(EntitiesDiffCallback()) { entity ->
        findNavController().navigate(EntitiesFragmentDirections.editEntity(entity.id))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fragmentScope.launch {
            viewModel.state.collect { state ->
                Log.d("AEEF", "State: $state")
                renderState(state, this@EntitiesFragment::renderer)
            }
        }
        viewModel.getAllEntities()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentEntitiesBinding.inflate(inflater, container, false)

        binding.rvEntities.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = entitiesAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }

        binding.addEntity.setOnClickListener {
            findNavController().navigate(EntitiesFragmentDirections.addEntity())
        }

        return binding.root
    }

    private fun renderer(state: EntitiesState) {
        if (state.entities.isNullOrEmpty()) {
            binding.emptyListMessage.show()
            binding.pbLoading.hide()
        } else {
            binding.emptyListMessage.hide()
            entitiesAdapter.submitList(state.entities)
            if (state.isLoading) {
                binding.pbLoading.show()
            } else {
                binding.pbLoading.hide()
            }
        }
    }
}
