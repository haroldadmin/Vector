package com.haroldadmin.sampleapp.entities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.haroldadmin.sampleapp.R
import com.haroldadmin.sampleapp.addEntity.AddEntityFragment
import com.haroldadmin.sampleapp.databinding.FragmentEntitiesBinding
import com.haroldadmin.sampleapp.utils.hide
import com.haroldadmin.sampleapp.utils.provider
import com.haroldadmin.sampleapp.utils.show
import com.haroldadmin.vector.VectorFragment
import com.haroldadmin.vector.withState

class EntitiesFragment : VectorFragment() {

    private lateinit var binding: FragmentEntitiesBinding

    private val viewModel by viewModels<EntitiesViewModel> {
        EntitiesViewModelFactory(provider().entitiesRepository, EntitiesState(
            entities = null,
            isLoading = true
        ))
    }

    private val entitiesAdapter = EntitiesAdapter(EntitiesDiffCallback())

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.state.observe(viewLifecycleOwner, Observer {
            renderState()
        })

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
            requireActivity().supportFragmentManager.commit {
                replace(R.id.fragmentContainer, AddEntityFragment())
                addToBackStack("entities")
            }
        }

        return binding.root
    }

    override fun renderState() = withState(viewModel) { state ->
        entitiesAdapter.submitList(state.entities)
        if (state.isLoading) {
            binding.pbLoading.show()
        } else {
            binding.pbLoading.hide()
        }
    }
}
