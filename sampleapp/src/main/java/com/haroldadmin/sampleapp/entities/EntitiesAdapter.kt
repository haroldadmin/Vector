package com.haroldadmin.sampleapp.entities

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.haroldadmin.sampleapp.CountingEntity
import com.haroldadmin.sampleapp.databinding.ItemEntityBinding

class EntitiesDiffCallback : DiffUtil.ItemCallback<CountingEntity>() {
    override fun areItemsTheSame(oldItem: CountingEntity, newItem: CountingEntity): Boolean =
        oldItem.name == newItem.name

    override fun areContentsTheSame(oldItem: CountingEntity, newItem: CountingEntity): Boolean {
        return oldItem.name == newItem.name &&
                oldItem.counter == newItem.counter &&
                oldItem.colour == newItem.colour
    }
}

class EntityViewHolder(private val binding: ItemEntityBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(entity: CountingEntity) {
        binding.entity = entity
        binding.executePendingBindings()
    }
}

class EntitiesAdapter(diffCallback: EntitiesDiffCallback) : ListAdapter<CountingEntity, EntityViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val binding = ItemEntityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
