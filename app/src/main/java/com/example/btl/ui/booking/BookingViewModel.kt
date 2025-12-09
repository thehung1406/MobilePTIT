package com.example.btl.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl.api.ApiClient
import com.example.btl.model.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor() : ViewModel() {

    private val _bookingState = MutableStateFlow<BookingState>(BookingState.Idle)
    val bookingState: StateFlow<BookingState> = _bookingState.asStateFlow()

    private val _propertyDetail = MutableStateFlow<Property?>(null)
    val propertyDetail: StateFlow<Property?> = _propertyDetail.asStateFlow()

    private val _roomTypeDetail = MutableStateFlow<RoomType?>(null)
    val roomTypeDetail: StateFlow<RoomType?> = _roomTypeDetail.asStateFlow()

    private val _availableRooms = MutableStateFlow<List<Room>>(emptyList())
    val availableRooms: StateFlow<List<Room>> = _availableRooms.asStateFlow()

    private val _totalPrice = MutableStateFlow(0)
    val totalPrice: StateFlow<Int> = _totalPrice.asStateFlow()

    private val _selectedRooms = MutableStateFlow<List<Int>>(emptyList())
    val selectedRooms: StateFlow<List<Int>> = _selectedRooms.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    fun loadBookingInfo(propertyId: Int, roomTypeId: Int) {
        viewModelScope.launch {
            try {
                _bookingState.value = BookingState.Loading

                val property = ApiClient.propertyService.getProperty(propertyId)
                _propertyDetail.value = property

                val roomTypes = ApiClient.roomTypeService.getRoomTypesByProperty(propertyId)
                val roomType = roomTypes.find { it.id == roomTypeId }
                _roomTypeDetail.value = roomType

                val allRooms = ApiClient.roomService.getRoomsByProperty(propertyId)
                val roomsOfType = allRooms.filter {
                    it.room_type_id == roomTypeId && it.is_active
                }
                _availableRooms.value = roomsOfType

                _bookingState.value = BookingState.Success
            } catch (e: Exception) {
                _error.value = "Lỗi tải thông tin: ${e.message}"
                _bookingState.value = BookingState.Error(e.message ?: "Unknown error")
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
        val availableRoomIds = _availableRooms.value
            .filter { it.is_active }
            .take(numberOfRooms)
            .mapNotNull { it.id }

        _selectedRooms.value = availableRoomIds
    }

    fun createBooking(
        roomIds: List<Int>,
        checkInDate: String,
        checkOutDate: String,
        numGuests: Int,
        totalPrice: Int
    ) {
        viewModelScope.launch {
            try {
                _bookingState.value = BookingState.Loading

                val bookingRequest = BookingRequest(
                    room_ids = roomIds,
                    checkin = checkInDate,
                    checkout = checkOutDate,
                    num_guests = numGuests,
                    price = totalPrice
                )

                // ✅ Trả về TaskResponse thay vì BookingResponse
                val response = ApiClient.bookingService.createBooking(bookingRequest)

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
}

// ✅ Sửa sealed class
sealed class BookingState {
    object Idle : BookingState()
    object Loading : BookingState()
    object Success : BookingState()
    data class BookingSuccess(val response: TaskResponse) : BookingState()  // ✅ Đổi type
    data class Error(val message: String) : BookingState()
}
