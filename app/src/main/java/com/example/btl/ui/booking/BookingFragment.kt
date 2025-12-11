package com.example.btl.ui.booking

import android.content.Context
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.btl.R
import com.example.btl.databinding.FragmentBookingBinding
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

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
    
    private lateinit var roomAdapter: AvailableRoomAdapter

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
        setupRecyclerView()
        setupClickListeners()
        
        // Hiển thị số đêm (readonly)
        val nights = TimeUnit.MILLISECONDS.toDays(checkOutDate - checkInDate).toInt().coerceAtLeast(1)
        binding.numberOfNights.text = "$nights đêm"

        observeViewModel()

        // Load booking info
        if (propertyId > 0 && roomTypeId > 0) {
            viewModel.loadPropertyDetail(propertyId, roomTypeId)

            val checkin = viewModel.formatDate(checkInDate)
            val checkout = viewModel.formatDate(checkOutDate)
            viewModel.loadAvailableRooms(roomTypeId, checkin, checkout)

            // Tính giá ban đầu (0 phòng)
            calculateAndDisplayPrice()
        }
    }
    
    private fun setupRecyclerView() {
        roomAdapter = AvailableRoomAdapter { selectedIds ->
            viewModel.updateSelectedRooms(selectedIds)
            calculateAndDisplayPrice()
        }
        binding.rvAvailableRooms.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = roomAdapter
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
        }
    }

    private fun setupClickListeners() {
        // Back button
        binding.backButton?.setOnClickListener {
            findNavController().navigateUp()
        }

        // Confirm booking button
        binding.confirmBookingButton.setOnClickListener {
            confirmBooking()
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
    
    private fun getCalculatedNights(): Int {
        return TimeUnit.MILLISECONDS.toDays(checkOutDate - checkInDate).toInt().coerceAtLeast(1)
    }

    private fun calculateAndDisplayPrice() {
        val nights = getCalculatedNights()
        val selectedCount = viewModel.selectedRoomIds.value.size
        
        val total = roomTypePrice * nights * selectedCount

        binding.roomTypePrice?.text = "${formatPrice(roomTypePrice)} / đêm"
        binding.numberOfRoomsText?.text = "$selectedCount phòng"
        binding.subtotalPrice?.text = formatPrice(roomTypePrice * nights * selectedCount)
        binding.totalPrice?.text = formatPrice(total)
        
        viewModel.calculateTotalPrice(roomTypePrice, nights)
    }


    private fun updateGuestsDisplay() {
        binding.numberOfGuestsValue?.text = numberOfGuests.toString()
    }

    private fun confirmBooking() {
        val selectedRoomIds = viewModel.selectedRoomIds.value
        val nights = getCalculatedNights()

        // Validate selected rooms
        if (selectedRoomIds.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn ít nhất 1 phòng", Toast.LENGTH_SHORT).show()
            return
        }

        // Show confirmation dialog
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận đặt phòng")
            .setMessage("""
                Bạn có chắc muốn đặt phòng với thông tin sau?
                
                Loại phòng: $roomTypeName
                Số phòng: ${selectedRoomIds.size}
                Số khách: $numberOfGuests
                Số đêm: $nights
                Tổng tiền: ${formatPrice(viewModel.totalPrice.value)}
            """.trimIndent())
            .setPositiveButton("Xác nhận") { _, _ ->
                processBooking()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun processBooking() {
        val selectedRoomIds = viewModel.selectedRoomIds.value
        
        val checkIn = viewModel.formatDate(checkInDate)
        val checkOut = viewModel.formatDate(checkOutDate)

        // Get token from SharedPreferences
        val prefs = requireContext().getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("auth_token", "") ?: ""

        viewModel.createBooking(
            token = token,
            roomIds = selectedRoomIds,
            checkInDate = checkIn,
            checkOutDate = checkOut,
            numGuests = numberOfGuests
        )
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Observe booking state
                launch {
                    viewModel.bookingState.collect { state ->
                        when (state) {
                            is BookingViewModel.BookingState.Loading -> {
                                binding.progressBar?.visibility = View.VISIBLE
                                binding.confirmBookingButton.isEnabled = false
                            }
                            is BookingViewModel.BookingState.Success -> {
                                binding.progressBar?.visibility = View.GONE
                                binding.confirmBookingButton.isEnabled = true
                                displayBookingInfo()
                            }
                            is BookingViewModel.BookingState.BookingSuccess -> {
                                binding.progressBar?.visibility = View.GONE
                                showBookingSuccess(state.response)
                            }
                            is BookingViewModel.BookingState.Error -> {
                                binding.progressBar?.visibility = View.GONE
                                binding.confirmBookingButton.isEnabled = true
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                            }
                            is BookingViewModel.BookingState.Idle -> {
                                binding.progressBar?.visibility = View.GONE
                            }
                        }
                    }
                }

                // Observe property details
                launch {
                    viewModel.property.collect { property ->
                        property?.let {
                            binding.propertyName?.text = it.name
                            binding.propertyAddress?.text = it.address

                            Glide.with(requireContext())
                                .load(it.image)
                                .placeholder(R.drawable.ic_hotel_placeholder)
                                .error(android.R.drawable.ic_menu_gallery)
                                .into(binding.propertyImage ?: return@let)
                        }
                    }
                }

                // Observe room type details
                launch {
                    viewModel.roomType.collect { roomType ->
                        roomType?.let {
                            binding.roomTypeName?.text = it.name
                            binding.maxOccupancy?.text = "Tối đa ${it.maxOccupancy} người"
                            binding.roomTypePrice?.text = "${formatPrice(it.price)} / đêm"
                        }
                    }
                }

                // Observe available rooms
                launch {
                    viewModel.availableRooms.collect { rooms ->
                        binding.availableRoomsCount?.text = "${rooms.size} phòng còn trống"
                        roomAdapter.submitList(rooms)

                        if (rooms.isEmpty()) {
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
    }

    private fun showBookingSuccess(response: com.example.btl.model.BookingResponse) {
        AlertDialog.Builder(requireContext())
            .setTitle("✅ Đặt phòng thành công!")
            .setMessage("""
                📋 Mã đặt phòng: ${response.bookingId}
                🔄 Trạng thái: ${response.status}
                ⏰ Hết hạn lúc: ${response.expiresAt}
                
                💡 Vui lòng hoàn tất thanh toán trước thời gian hết hạn.
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
