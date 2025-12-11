package com.example.btl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.databinding.ItemRoomTypeBinding
import com.example.btl.model.RoomTypeWithRooms
import java.text.NumberFormat
import java.util.*

class RoomTypeAdapter(
    private val onItemClick: (RoomTypeWithRooms) -> Unit
) : ListAdapter<RoomTypeWithRooms, RoomTypeAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRoomTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        private val binding: ItemRoomTypeBinding,
        private val onItemClick: (RoomTypeWithRooms) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(roomType: RoomTypeWithRooms) {
            binding.roomTypeName.text = roomType.name
            binding.roomTypePrice.text = formatPrice(roomType.price)
            binding.maxOccupancy.text = "Tối đa ${roomType.maxOccupancy} người"
            binding.availableRooms.text = "${roomType.rooms.size} phòng"

            binding.root.setOnClickListener {
                onItemClick(roomType)
            }
        }

        private fun formatPrice(price: Int): String {
            return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(price)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RoomTypeWithRooms>() {
        override fun areItemsTheSame(oldItem: RoomTypeWithRooms, newItem: RoomTypeWithRooms): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: RoomTypeWithRooms, newItem: RoomTypeWithRooms): Boolean {
            return oldItem == newItem
        }
    }
}
