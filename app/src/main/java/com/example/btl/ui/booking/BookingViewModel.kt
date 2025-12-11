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

    private val _property = MutableStateFlow<Property?>(null)
    val property: StateFlow<Property?> = _property.asStateFlow()

    private val _roomType = MutableStateFlow<RoomTypeWithRooms?>(null)
    val roomType: StateFlow<RoomTypeWithRooms?> = _roomType.asStateFlow()

    // ✅ FIX 1: Đổi từ AvailableRoomsResponse → List<AvailableRoom>
    private val _availableRooms = MutableStateFlow<List<AvailableRoom>>(emptyList())
    val availableRooms: StateFlow<List<AvailableRoom>> = _availableRooms.asStateFlow()

    private val _totalPrice = MutableStateFlow(0)
    val totalPrice: StateFlow<Int> = _totalPrice.asStateFlow()

    private val _selectedRoomIds = MutableStateFlow<List<Int>>(emptyList())
    val selectedRoomIds: StateFlow<List<Int>> = _selectedRoomIds.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    fun loadPropertyDetail(propertyId: Int, roomTypeId: Int) {
        viewModelScope.launch {
            try {
                _bookingState.value = BookingState.Loading

                val propertyDetail = ApiClient.propertyService.getPropertyDetail(propertyId)

                _property.value = propertyDetail.toProperty()

                val foundRoomType = propertyDetail.roomTypes.find { it.id == roomTypeId }
                _roomType.value = foundRoomType

                _bookingState.value = BookingState.Success
            } catch (e: Exception) {
                _error.value = "Lỗi tải thông tin: ${e.message}"
                _bookingState.value = BookingState.Error(e.message ?: "Unknown error")
            }
        }
    }

    // ✅ FIX 2: Sửa loadAvailableRooms
    fun loadAvailableRooms(
        roomTypeId: Int,
        checkin: String,
        checkout: String
    ) {
        viewModelScope.launch {
            try {
                val response = ApiClient.roomService.getAvailableRooms(
                    roomTypeId = roomTypeId,
                    checkin = checkin,
                    checkout = checkout
                )
                // ✅ Lấy list availableRooms từ response
                _availableRooms.value = response
                
                // Tự động chọn phòng nếu có đủ phòng trống
                // selectRooms(1) // Tạm thời để user tự chọn hoặc logic khác xử lý
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

    // ✅ FIX 3: Sửa selectRooms
    fun selectRooms(numberOfRooms: Int) {
        val selectedIds = _availableRooms.value
            .take(numberOfRooms)
            .map { it.roomId }  // ✅ Đổi it.id → it.roomId

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
