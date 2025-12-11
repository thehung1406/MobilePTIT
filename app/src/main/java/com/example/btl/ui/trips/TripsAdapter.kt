package com.example.btl.ui.trips

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.databinding.ItemTripBinding
import com.example.btl.model.BookingResponse
import java.text.SimpleDateFormat
import java.util.*

class TripsAdapter(
    private var bookings: List<BookingResponse>,
    private val onCancelClick: (BookingResponse) -> Unit,
    private val onPayClick: (BookingResponse) -> Unit
) : RecyclerView.Adapter<TripsAdapter.TripViewHolder>() {

    fun updateData(newBookings: List<BookingResponse>) {
        bookings = newBookings
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val binding = ItemTripBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount(): Int = bookings.size

    inner class TripViewHolder(private val binding: ItemTripBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(booking: BookingResponse) {
            binding.tvBookingId.text = "Mã đặt phòng: #${booking.bookingId}"
            
            // Format status
            val status = booking.status?.uppercase() ?: "UNKNOWN"
            binding.tvStatus.text = status
            
            // Set status color
            when (status) {
                "CONFIRMED" -> binding.tvStatus.setTextColor(Color.parseColor("#4CAF50")) // Green
                "PENDING" -> binding.tvStatus.setTextColor(Color.parseColor("#FF9800")) // Orange
                "CANCELLED" -> binding.tvStatus.setTextColor(Color.parseColor("#F44336")) // Red
                else -> binding.tvStatus.setTextColor(Color.GRAY)
            }

            // Display room info
            // With new JSON format, we might not have room names directly
            val roomName = if (!booking.rooms.isNullOrEmpty()) {
                booking.rooms.firstOrNull()?.roomName ?: "Phòng không xác định"
            } else if (!booking.selectedRooms.isNullOrEmpty()) {
                "Phòng ID: ${booking.selectedRooms.joinToString(", ")}"
            } else {
                "Thông tin phòng chưa có"
            }
            binding.tvRoomName.text = roomName

            // Display date
            val displayCheckin = booking.displayCheckin
            val displayCheckout = booking.displayCheckout
            
            if (displayCheckin != null && displayCheckout != null) {
                binding.tvDate.text = "${formatDate(displayCheckin)} - ${formatDate(displayCheckout)}"
            } else {
                 binding.tvDate.text = ""
            }
            
            // Show/Hide buttons based on status
            if (status == "PENDING" || status == "CONFIRMED") {
                binding.layoutActions.visibility = View.VISIBLE
                
                binding.btnPay.visibility = if (status == "PENDING") View.VISIBLE else View.GONE
                
                binding.btnCancel.setOnClickListener { onCancelClick(booking) }
                binding.btnPay.setOnClickListener { onPayClick(booking) }
            } else {
                binding.layoutActions.visibility = View.GONE
            }
            
            // Show expires info if pending
            if (status == "PENDING" && booking.expiresAt != null) {
                 binding.tvExpiresAt.visibility = View.VISIBLE
                 binding.tvExpiresAt.text = "Hết hạn: ${formatDateTime(booking.expiresAt)}"
            } else {
                 binding.tvExpiresAt.visibility = View.GONE
            }
        }
        
        private fun formatDate(dateStr: String?): String {
            if (dateStr.isNullOrEmpty()) return ""
            return try {
                // Try format yyyy-MM-dd
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi"))
                val date = inputFormat.parse(dateStr)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateStr
            }
        }
        
        private fun formatDateTime(dateTimeStr: String?): String {
             if (dateTimeStr.isNullOrEmpty()) return ""
            return try {
                // Try ISO 8601 or similar variants
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                val outputFormat = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale("vi"))
                val date = inputFormat.parse(dateTimeStr)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateTimeStr
            }
        }
    }
}
