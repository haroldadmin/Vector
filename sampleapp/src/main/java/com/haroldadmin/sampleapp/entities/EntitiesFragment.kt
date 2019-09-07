package com.haroldadmin.sampleapp.entities

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.haroldadmin.sampleapp.AppViewModel
import com.haroldadmin.sampleapp.databinding.FragmentEntitiesBinding
import com.haroldadmin.sampleapp.utils.hide
import com.haroldadmin.sampleapp.utils.show
import com.haroldadmin.vector.VectorFragment
import com.haroldadmin.vector.activityViewModel
import com.haroldadmin.vector.fragmentViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

class EntitiesFragment : VectorFragment() {

    @Inject lateinit var viewModelFactory: EntitiesViewModel.Factory
    @Inject lateinit var appViewModelFactory: AppViewModel.Factory

    private lateinit var binding: FragmentEntitiesBinding

    private val viewModel: EntitiesViewModel by fragmentViewModel { initialState, _ ->
        viewModelFactory.create(initialState)
    }

    private val appViewModel: AppViewModel by activityViewModel { initialState, _ ->
        appViewModelFactory.create(initialState)
    }

    private val entitiesAdapter = EntitiesAdapter(EntitiesDiffCallback()) { entity ->
        findNavController().navigate(EntitiesFragmentDirections.editEntity(entity.id))
    }

    override fun onAttach(context: Context) {
//        inject()
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        renderState(viewModel) { state ->
            entitiesAdapter.submitList(state.entities)
            if (state.entities.isNullOrEmpty()) {
                binding.emptyListMessage.show()
                binding.pbLoading.hide()
            } else {
                binding.emptyListMessage.hide()
                if (state.isLoading) {
                    binding.pbLoading.show()
                } else {
                    binding.pbLoading.hide()
                }
            }
            appViewModel.updateNumberOfEntities()
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
}
