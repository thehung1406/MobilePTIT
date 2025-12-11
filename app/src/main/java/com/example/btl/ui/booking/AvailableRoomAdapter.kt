package com.example.btl.ui.booking

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.databinding.ItemRoomCheckboxBinding
import com.example.btl.model.AvailableRoom

class AvailableRoomAdapter(
    private val onRoomSelectionChanged: (List<Int>) -> Unit
) : RecyclerView.Adapter<AvailableRoomAdapter.RoomViewHolder>() {

    private var rooms = listOf<AvailableRoom>()
    private val selectedRoomIds = mutableSetOf<Int>()

    fun submitList(newRooms: List<AvailableRoom>) {
        rooms = newRooms
        selectedRoomIds.clear()
        notifyDataSetChanged()
    }
    
    // Hàm để auto-select n phòng đầu tiên (nếu cần)
    fun selectFirstNRooms(n: Int) {
        selectedRoomIds.clear()
        rooms.take(n).forEach { selectedRoomIds.add(it.roomId) }
        notifyDataSetChanged()
        onRoomSelectionChanged(selectedRoomIds.toList())
    }
    
    // Hàm lấy số lượng phòng đang được chọn
    fun getSelectedCount(): Int = selectedRoomIds.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoomViewHolder {
        val binding = ItemRoomCheckboxBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RoomViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoomViewHolder, position: Int) {
        holder.bind(rooms[position])
    }

    override fun getItemCount(): Int = rooms.size

    inner class RoomViewHolder(private val binding: ItemRoomCheckboxBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(room: AvailableRoom) {
            binding.cbRoom.text = room.roomName
            binding.cbRoom.isChecked = selectedRoomIds.contains(room.roomId)

            binding.cbRoom.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedRoomIds.add(room.roomId)
                } else {
                    selectedRoomIds.remove(room.roomId)
                }
                onRoomSelectionChanged(selectedRoomIds.toList())
            }
        }
    }
}
