package com.example.btl.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl.api.ApiClient
import com.example.btl.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class BookingViewModel : ViewModel() {

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    // ✅ Giữ nguyên interface cũ
    private val _property = MutableStateFlow<Property?>(null)
    val property: StateFlow<Property?> = _property.asStateFlow()

    private val _roomType = MutableStateFlow<RoomTypeWithRooms?>(null)
    val roomType: StateFlow<RoomTypeWithRooms?> = _roomType.asStateFlow()

    private val _availableRooms = MutableStateFlow<List<RoomInfo>>(emptyList())
    val availableRooms: StateFlow<List<RoomInfo>> = _availableRooms.asStateFlow()

    private val _totalPrice = MutableStateFlow(0)
    val totalPrice: StateFlow<Int> = _totalPrice.asStateFlow()

    private val _selectedRoomIds = MutableStateFlow<List<Int>>(emptyList())
    val selectedRoomIds: StateFlow<List<Int>> = _selectedRoomIds.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    // ✅ Sửa logic: Load property + tìm room type
    fun loadPropertyDetail(propertyId: Int, roomTypeId: Int) {
        viewModelScope.launch {
            try {
                _bookingState.value = BookingState.Loading

                val propertyDetail = ApiClient.propertyService.getPropertyDetail(propertyId)

                // Tách ra giống cũ
                _property.value = propertyDetail.property

                // Tìm room type cụ thể
                val foundRoomType = propertyDetail.roomTypes.find { it.id == roomTypeId }
                _roomType.value = foundRoomType

                _bookingState.value = BookingState.Success
            } catch (e: Exception) {
                _error.value = "Lỗi tải thông tin: ${e.message}"
                _bookingState.value = BookingState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun loadAvailableRooms(
        roomTypeId: Int,
        checkin: String,
        checkout: String
    ) {
        viewModelScope.launch {
            try {
                val rooms = ApiClient.roomService.getAvailableRooms(
                    roomTypeId = roomTypeId,
                    checkin = checkin,
                    checkout = checkout
                )
                _availableRooms.value = rooms
            } catch (e: Exception) {
                _error.value = "Lỗi tải phòng trống: ${e.message}"
            }
        }
    }

    fun calculateTotalPrice(
        roomTypePrice: Int,
        numberOfNights: Int,
        numberOfRooms: Int
    ) {
        _totalPrice.value = roomTypePrice * numberOfNights * numberOfRooms
    }

    fun selectRooms(numberOfRooms: Int) {
        val selectedIds = _availableRooms.value
            .filter { it.isActive }
            .take(numberOfRooms)
            .map { it.id }

        _selectedRoomIds.value = selectedIds
    }

    fun createBooking(
        token: String,
        roomIds: List<Int>,
        checkInDate: String,
        checkOutDate: String,
        numGuests: Int
    ) {
        viewModelScope.launch {
            try {
                _bookingState.value = BookingState.Loading

                val bookingRequest = BookingRequest(
                    roomIds = roomIds,
                    checkin = checkInDate,
                    checkout = checkOutDate,
                    numGuests = numGuests
                )

                val response = ApiClient.bookingService.createBooking(
                    token = "Bearer $token",
                    request = bookingRequest
                )

                _bookingState.value = BookingState.BookingSuccess(response)
            } catch (e: Exception) {
                _error.value = "Lỗi đặt phòng: ${e.message}"
                _bookingState.value = BookingState.Error(e.message ?: "Booking failed")
            }
        }
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun calculateNumberOfNights(checkin: String, checkout: String): Int {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val checkinDate = sdf.parse(checkin)
            val checkoutDate = sdf.parse(checkout)

            val diff = checkoutDate!!.time - checkinDate!!.time
            (diff / (1000 * 60 * 60 * 24)).toInt()
        } catch (e: Exception) {
            1
        }
    }

    sealed class BookingState {
        object Idle : BookingState()
        object Loading : BookingState()
        object Success : BookingState()
        data class BookingSuccess(val response: BookingResponse) : BookingState()
        data class Error(val message: String) : BookingState()
    }
}
