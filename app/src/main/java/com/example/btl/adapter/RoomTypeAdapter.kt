package com.example.btl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.databinding.ItemRoomTypeBinding
import com.example.btl.model.RoomTypeWithRooms
import java.text.NumberFormat
import java.util.Locale

class RoomTypeAdapter(
    private val onItemClick: (RoomTypeWithRooms) -> Unit
) : ListAdapter<RoomTypeWithRooms, RoomTypeAdapter.RoomTypeViewHolder>(RoomTypeDiffCallback()) {

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

        fun bind(roomType: RoomTypeWithRooms) {
            binding.apply {
                roomTypeName.text = roomType.name

                val formatter = NumberFormat.getNumberInstance(Locale("vi", "VN"))
                roomTypePrice.text = "${formatter.format(roomType.price)} ₫"

                // ✅ Hiển thị số phòng trống
                val availableCount = roomType.rooms.filter { it.isActive }.size
                maxOccupancy.text = "Tối đa ${roomType.maxOccupancy} người • $availableCount phòng trống"

                // ✅ Disable nếu hết phòng
                root.isEnabled = availableCount > 0
                root.alpha = if (availableCount > 0) 1.0f else 0.5f

                root.setOnClickListener {
                    if (availableCount > 0) {
                        onItemClick(roomType)
                    }
                }
            }
        }

    }

    private class RoomTypeDiffCallback : DiffUtil.ItemCallback<RoomTypeWithRooms>() {
        override fun areItemsTheSame(
            oldItem: RoomTypeWithRooms,
            newItem: RoomTypeWithRooms
        ): Boolean = oldItem.id == newItem.id

        override fun areContentsTheSame(
            oldItem: RoomTypeWithRooms,
            newItem: RoomTypeWithRooms
        ): Boolean = oldItem == newItem
    }
}
