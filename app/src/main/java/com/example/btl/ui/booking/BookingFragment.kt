package com.example.btl.ui.booking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.btl.R
import com.example.btl.databinding.FragmentBookingBinding
import com.example.btl.model.TaskResponse
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class BookingFragment : Fragment() {

    private var _binding: FragmentBookingBinding? = null
    private val binding get() = _binding!!

    private val viewModel: BookingViewModel by viewModels()

    // Arguments from navigation
    private var roomTypeId: Int = 0
    private var propertyId: Int = 0
    private var roomTypeName: String = ""
    private var roomTypePrice: Int = 0
    private var checkInDate: Long = 0
    private var checkOutDate: Long = 0
    private var numberOfGuests: Int = 2
    private var numberOfRooms: Int = 1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgumentsData()
        setupClickListeners()
        observeViewModel()

        // Load booking info
        if (propertyId > 0 && roomTypeId > 0) {
            viewModel.loadBookingInfo(propertyId, roomTypeId)
            viewModel.selectRooms(numberOfRooms)
            calculateAndDisplayPrice()
        }
    }

    private fun getArgumentsData() {
        arguments?.let {
            roomTypeId = it.getInt("roomTypeId", 0)
            propertyId = it.getInt("propertyId", 0)
            roomTypeName = it.getString("roomTypeName", "")
            roomTypePrice = it.getInt("roomTypePrice", 0)
            checkInDate = it.getLong("checkInDate", System.currentTimeMillis())
            checkOutDate = it.getLong("checkOutDate", System.currentTimeMillis() + TimeUnit.DAYS.toMillis(1))
            numberOfGuests = it.getInt("numberOfGuests", 2)
            numberOfRooms = it.getInt("numberOfRooms", 1)
        }
    }

    private fun setupClickListeners() {
        // Back button
        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        // Confirm booking button
        binding.confirmBookingButton.setOnClickListener {
            confirmBooking()
        }

        // Increase/Decrease rooms
        binding.decreaseRoomsButton?.setOnClickListener {
            if (numberOfRooms > 1) {
                numberOfRooms--
                updateRoomsDisplay()
                calculateAndDisplayPrice()
                viewModel.selectRooms(numberOfRooms)
            }
        }

        binding.increaseRoomsButton?.setOnClickListener {
            if (numberOfRooms < 5) { // Max 5 rooms
                numberOfRooms++
                updateRoomsDisplay()
                calculateAndDisplayPrice()
                viewModel.selectRooms(numberOfRooms)
            }
        }

        // Increase/Decrease guests
        binding.decreaseGuestsButton?.setOnClickListener {
            if (numberOfGuests > 1) {
                numberOfGuests--
                updateGuestsDisplay()
            }
        }

        binding.increaseGuestsButton?.setOnClickListener {
            if (numberOfGuests < 10) { // Max 10 guests
                numberOfGuests++
                updateGuestsDisplay()
            }
        }
    }

    private fun calculateAndDisplayPrice() {
        val nights = TimeUnit.MILLISECONDS.toDays(checkOutDate - checkInDate).toInt()
        val total = roomTypePrice * nights * numberOfRooms

        binding.roomTypePrice?.text = "${formatPrice(roomTypePrice)} / đêm"
        binding.numberOfNights?.text = "$nights đêm"
        binding.numberOfRoomsText?.text = "$numberOfRooms phòng"
        binding.subtotalPrice?.text = formatPrice(roomTypePrice * nights)
        binding.totalPrice?.text = formatPrice(total)

        viewModel.calculateTotalPrice(roomTypePrice, nights, numberOfRooms)
    }

    private fun updateRoomsDisplay() {
        binding.numberOfRoomsValue?.text = numberOfRooms.toString()
    }

    private fun updateGuestsDisplay() {
        binding.numberOfGuestsValue?.text = numberOfGuests.toString()
    }

    private fun confirmBooking() {
        val selectedRoomIds = viewModel.selectedRooms.value

        // Validate selected rooms
        if (selectedRoomIds.isEmpty()) {
            Toast.makeText(requireContext(), "Không có phòng trống", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedRoomIds.size < numberOfRooms) {
            Toast.makeText(
                requireContext(),
                "Chỉ còn ${selectedRoomIds.size} phòng trống",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Show confirmation dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận đặt phòng")
            .setMessage("""
                Bạn có chắc muốn đặt phòng với thông tin sau?
                
                Loại phòng: $roomTypeName
                Số phòng: $numberOfRooms
                Số khách: $numberOfGuests
                Check-in: ${formatDateDisplay(checkInDate)}
                Check-out: ${formatDateDisplay(checkOutDate)}
                
                Tổng tiền: ${formatPrice(viewModel.totalPrice.value)}
            """.trimIndent())
            .setPositiveButton("Xác nhận") { _, _ ->
                processBooking()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun processBooking() {
        val selectedRoomIds = viewModel.selectedRooms.value
        val checkIn = viewModel.formatDate(checkInDate)
        val checkOut = viewModel.formatDate(checkOutDate)
        val totalPrice = viewModel.totalPrice.value

        viewModel.createBooking(
            roomIds = selectedRoomIds,
            checkInDate = checkIn,
            checkOutDate = checkOut,
            numGuests = numberOfGuests,
            totalPrice = totalPrice
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Observe booking state
                launch {
                    viewModel.bookingState.collect { state ->
                        when (state) {
                            is BookingState.Loading -> {
                                binding.progressBar?.visibility = View.VISIBLE
                                binding.confirmBookingButton.isEnabled = false
                            }
                            is BookingState.Success -> {
                                binding.progressBar?.visibility = View.GONE
                                binding.confirmBookingButton.isEnabled = true
                                displayBookingInfo()
                            }
                            is BookingState.BookingSuccess -> {
                                binding.progressBar?.visibility = View.GONE
                                showBookingSuccess(state.response)
                            }
                            is BookingState.Error -> {
                                binding.progressBar?.visibility = View.GONE
                                binding.confirmBookingButton.isEnabled = true
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                            }
                            is BookingState.Idle -> {
                                binding.progressBar?.visibility = View.GONE
                            }
                        }
                    }
                }

                // Observe property details
                launch {
                    viewModel.propertyDetail.collect { property ->
                        property?.let {
                            binding.propertyName?.text = it.name
                            binding.propertyAddress?.text = it.address

                            Glide.with(requireContext())
                                .load(it.image)
                                .placeholder(R.drawable.ic_hotel_placeholder)
                                .error(android.R.drawable.ic_menu_gallery)
                                .into(binding.propertyImage)
                        }
                    }
                }

                // Observe room type details
                launch {
                    viewModel.roomTypeDetail.collect { roomType ->
                        roomType?.let {
                            binding.roomTypeName?.text = it.name
                            binding.maxOccupancy?.text = "Tối đa ${it.max_occupancy} người"
                        }
                    }
                }

                // Observe available rooms
                launch {
                    viewModel.availableRooms.collect { rooms ->
                        binding.availableRoomsCount?.text = "${rooms.size} phòng còn trống"

                        // Hiển thị warning nếu không đủ phòng
                        if (rooms.size < numberOfRooms) {
                            binding.availableRoomsCount?.setTextColor(
                                resources.getColor(android.R.color.holo_red_dark, null)
                            )
                        } else {
                            binding.availableRoomsCount?.setTextColor(
                                resources.getColor(android.R.color.holo_green_dark, null)
                            )
                        }
                    }
                }

                // Observe errors
                launch {
                    viewModel.error.collect { errorMessage ->
                        if (errorMessage.isNotEmpty()) {
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun displayBookingInfo() {
        binding.checkInDate?.text = formatDateDisplay(checkInDate)
        binding.checkOutDate?.text = formatDateDisplay(checkOutDate)
        binding.numberOfGuestsValue?.text = numberOfGuests.toString()
        binding.numberOfRoomsValue?.text = numberOfRooms.toString()
    }

    private fun showBookingSuccess(response: TaskResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle("✅ Yêu cầu đặt phòng đã được gửi!")
            .setMessage("""
                ${response.message}
                
                📋 Mã theo dõi: ${response.task_id}
                🔄 Trạng thái: ${getStatusText(response.status)}
                
                💡 Chúng tôi sẽ xác nhận đơn đặt phòng của bạn trong vài phút.
                📧 Kiểm tra email để nhận thông tin chi tiết.
            """.trimIndent())
            .setPositiveButton("Xem lịch sử đặt phòng") { _, _ ->
                // TODO: Navigate to booking history
                findNavController().navigateUp()
            }
            .setNegativeButton("Đóng") { _, _ ->
                findNavController().navigateUp()
            }
            .setCancelable(false)
            .show()
    }

    private fun getStatusText(status: String): String {
        return when (status.lowercase()) {
            "queued" -> "Đang xếp hàng"
            "processing" -> "Đang xử lý"
            "completed" -> "Hoàn thành"
            "failed" -> "Thất bại"
            else -> status
        }
    }

    private fun formatPrice(price: Int): String {
        return NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(price)
    }

    private fun formatDateDisplay(timestamp: Long): String {
        val sdf = SimpleDateFormat("EEE, dd/MM/yyyy", Locale("vi"))
        return sdf.format(Date(timestamp))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
