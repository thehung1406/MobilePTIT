package com.example.btl.ui.trips

import android.graphics.Color
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.btl.api.HotelNameCache
import com.example.btl.databinding.ItemTripBinding
import com.example.btl.model.BookingResponse
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class TripsAdapter(
    private var bookings: List<BookingResponse>,
    private val onCancelClick: (BookingResponse) -> Unit,
    private val onPayClick: (BookingResponse) -> Unit,
    private val onCountdownFinish: (BookingResponse) -> Unit
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

        private var countDownTimer: CountDownTimer? = null

        fun bind(booking: BookingResponse) {
            // Cancel any existing timer
            countDownTimer?.cancel()

            binding.tvBookingId.text = "Mã đặt phòng: #${booking.bookingId}"

            // Original status from API
            val status = booking.status?.uppercase(Locale.ROOT) ?: "UNKNOWN"

            // Set display text and color based on status
            val statusText = when (status) {
                "CONFIRMED" -> "ĐÃ THANH TOÁN"
                "PENDING" -> "CHỜ THANH TOÁN"
                "CANCELLED" -> "ĐÃ HỦY"
                else -> status
            }
            binding.tvStatus.text = statusText

            when (status) {
                "CONFIRMED" -> binding.tvStatus.setTextColor(Color.parseColor("#4CAF50")) // Green
                "PENDING" -> binding.tvStatus.setTextColor(Color.parseColor("#FF9800")) // Orange
                "CANCELLED" -> binding.tvStatus.setTextColor(Color.parseColor("#F44336")) // Red
                else -> binding.tvStatus.setTextColor(Color.GRAY)
            }

            // Display hotel name from cache. Fallback to a default text if not found.
            val firstRoomId = booking.selectedRooms?.firstOrNull() ?: 0
            val hotelName = HotelNameCache.getHotelNameForRoom(itemView.context, firstRoomId)
            binding.tvHotelName.text = hotelName ?: "Khách sạn không xác định"

            // Display room info. Use selectedRooms as it's more reliable now.
            val roomNames = if (!booking.selectedRooms.isNullOrEmpty()) {
                "Phòng: ${booking.selectedRooms.joinToString(", ")}"
            } else {
                "Thông tin phòng chưa có"
            }
            binding.tvRoomName.text = roomNames

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

            // Handle countdown timer for PENDING status
            if (status == "PENDING" && booking.expiresAt != null) {
                binding.tvExpiresAt.visibility = View.VISIBLE
                binding.tvExpiresAt.text = "Hết hạn: ${formatDateTime(booking.expiresAt)}"

                val expiresAtMillis = parseDateTime(booking.expiresAt)?.time ?: 0
                val currentTimeMillis = System.currentTimeMillis()
                val remainingTime = expiresAtMillis - currentTimeMillis

                if (remainingTime > 0) {
                    binding.countdownTimer.visibility = View.VISIBLE
                    countDownTimer = object : CountDownTimer(remainingTime, 1000) {
                        override fun onTick(millisUntilFinished: Long) {
                            val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)
                            val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60
                            binding.countdownTimer.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
                        }

                        override fun onFinish() {
                            binding.countdownTimer.visibility = View.GONE
                            onCountdownFinish(booking)
                        }
                    }.start()
                } else {
                    binding.countdownTimer.visibility = View.GONE
                    onCountdownFinish(booking)
                }
            } else {
                binding.tvExpiresAt.visibility = View.GONE
                binding.countdownTimer.visibility = View.GONE
            }
        }

        private fun formatDate(dateStr: String?): String {
            if (dateStr.isNullOrEmpty()) return ""
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale("vi"))
                val date = inputFormat.parse(dateStr)
                outputFormat.format(date!!)
            } catch (e: Exception) {
                dateStr
            }
        }

        private fun parseDateTime(dateTimeStr: String?): Date? {
            if (dateTimeStr.isNullOrEmpty()) return null
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                inputFormat.parse(dateTimeStr)
            } catch (e: Exception) {
                null
            }
        }

        private fun formatDateTime(dateTimeStr: String?): String {
            val date = parseDateTime(dateTimeStr) ?: return ""
            return try {
                val outputFormat = SimpleDateFormat("HH:mm dd/MM/yyyy", Locale("vi"))
                outputFormat.format(date)
            } catch (e: Exception) {
                dateTimeStr!!
            }
        }
    }
}