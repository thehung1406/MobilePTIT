package com.example.btl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.databinding.ItemRoomTypeBinding
import com.example.btl.model.RoomType
import java.text.NumberFormat
import java.util.*

class RoomTypeAdapter(
    private val onItemClick: (RoomType) -> Unit
) : ListAdapter<RoomType, RoomTypeAdapter.RoomTypeViewHolder>(RoomTypeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomTypeViewHolder {
        val binding = ItemRoomTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomTypeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomTypeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RoomTypeViewHolder(
        private val binding: ItemRoomTypeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(roomType: RoomType) {
            binding.apply {
                // Set room type details
                roomTypeName.text = roomType.name
                roomMaxOccupancy.text = "Tối đa ${roomType.max_occupancy} người"

                // Format price in Vietnamese currency
                val formattedPrice = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
                    .format(roomType.price)
                roomPrice.text = "$formattedPrice / đêm"

                // Availability status
                availabilityText?.text = if (roomType.is_active) {
                    "Còn phòng"
                } else {
                    "Hết phòng"
                }

                // Set availability color
                availabilityText?.setTextColor(
                    if (roomType.is_active) {
                        itemView.context.getColor(android.R.color.holo_green_dark)
                    } else {
                        itemView.context.getColor(android.R.color.holo_red_dark)
                    }
                )

                // Enable/disable booking based on availability
                bookButton.isEnabled = roomType.is_active
                bookButton.alpha = if (roomType.is_active) 1.0f else 0.5f

                // Set click listeners
                bookButton.setOnClickListener {
                    if (roomType.is_active) {
                        onItemClick(roomType)
                    }
                }

                root.setOnClickListener {
                    if (roomType.is_active) {
                        onItemClick(roomType)
                    }
                }
            }
        }
    }

    class RoomTypeDiffCallback : DiffUtil.ItemCallback<RoomType>() {
        override fun areItemsTheSame(oldItem: RoomType, newItem: RoomType): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RoomType, newItem: RoomType): Boolean {
            return oldItem == newItem
        }
    }
}
