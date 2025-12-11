package com.example.btl.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.btl.R
import com.example.btl.databinding.ItemHotelBinding
import com.example.btl.model.Property

class HotelAdapter(
    private val onItemClick: (Property) -> Unit
) : ListAdapter<Property, HotelAdapter.HotelViewHolder>(HotelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotelViewHolder {
        val binding = ItemHotelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HotelViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HotelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HotelViewHolder(
        private val binding: ItemHotelBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(property: Property) {
            binding.apply {
                // Set property details
                hotelName.text = property.name
                hotelAddress.text = property.address

                // Load hotel image
                Glide.with(itemView.context)
                    .load(property.image)
                    .placeholder(R.drawable.ic_hotel_placeholder)
                    .error(R.drawable.ic_hotel_placeholder)
                    .centerCrop()
                    .into(hotelImage)

                // Set click listener
                root.setOnClickListener {
                    onItemClick(property)
                }

                // Optional: Add favorite button functionality
                favoriteButton?.setOnClickListener {
                    // TODO: Implement favorite functionality
                }
            }
        }
    }

    class HotelDiffCallback : DiffUtil.ItemCallback<Property>() {
        override fun areItemsTheSame(oldItem: Property, newItem: Property): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Property, newItem: Property): Boolean {
            return oldItem == newItem
        }
    }
}
