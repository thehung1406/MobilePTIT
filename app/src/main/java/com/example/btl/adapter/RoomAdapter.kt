package com.example.btl.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.btl.R
import com.example.btl.databinding.ItemRoomBinding
import com.example.btl.model.Room

class RoomAdapter(
    private val onItemClick: (Room) -> Unit
) : ListAdapter<Room, RoomAdapter.RoomViewHolder>(RoomDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RoomViewHolder(
        private val binding: ItemRoomBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(room: Room) {
            binding.apply {
                roomName.text = room.name

                // Set availability
                availabilityBadge?.text = if (room.isActive) {
                    "Có sẵn"
                } else {
                    "Đã đặt"
                }

                availabilityBadge?.setBackgroundResource(
                    if (room.isActive) {
                        R.drawable.bg_available
                    } else {
                        R.drawable.bg_unavailable
                    }
                )

                // Load room image
                Glide.with(itemView.context)
                    .load(room.image)
                    .placeholder(R.drawable.ic_room_placeholder)
                    .error(R.drawable.ic_room_placeholder)
                    .centerCrop()
                    .into(roomImage)

                // Set click listener
                root.setOnClickListener {
                    if (room.isActive) {
                        onItemClick(room)
                    }
                }

                root.isEnabled = room.isActive
                root.alpha = if (room.isActive) 1.0f else 0.6f
            }
        }
    }

    class RoomDiffCallback : DiffUtil.ItemCallback<Room>() {
        override fun areItemsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Room, newItem: Room): Boolean {
            return oldItem == newItem
        }
    }
}
