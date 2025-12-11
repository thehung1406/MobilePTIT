package com.example.btl.ui.hotel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl.api.ApiClient
import com.example.btl.model.Property
import com.example.btl.model.RoomTypeWithRooms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HotelDetailViewModel : ViewModel() {

    private val _property = MutableStateFlow<Property?>(null)
    val property: StateFlow<Property?> = _property.asStateFlow()

    private val _roomTypes = MutableStateFlow<List<RoomTypeWithRooms>>(emptyList())
    val roomTypes: StateFlow<List<RoomTypeWithRooms>> = _roomTypes.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var checkInDate: Long = 0
    private var checkOutDate: Long = 0

    fun loadPropertyDetail(propertyId: Int, checkIn: Long = 0, checkOut: Long = 0) {
        this.checkInDate = checkIn
        this.checkOutDate = checkOut

        viewModelScope.launch {
            _isLoading.value = true

            Log.d("HotelDetailVM", "Loading property detail for ID: $propertyId")

            try {
                val response = ApiClient.propertyService.getPropertyDetail(propertyId)

                Log.d("HotelDetailVM", "Property name: ${response.name}")
                Log.d("HotelDetailVM", "Room types count: ${response.roomTypes.size}")

                _property.value = response.toProperty()

                // ✅ Load available rooms cho từng room type
                val roomTypesWithAvailability = if (checkIn > 0 && checkOut > 0) {
                    loadAvailableRoomsForEachType(response.roomTypes, checkIn, checkOut)
                } else {
                    response.roomTypes
                }

                _roomTypes.value = roomTypesWithAvailability
                _error.value = ""

            } catch (e: Exception) {
                Log.e("HotelDetailVM", "Error loading property detail", e)
                e.printStackTrace()
                _error.value = "Lỗi tải thông tin khách sạn: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }


    private suspend fun loadAvailableRoomsForEachType(
        roomTypes: List<RoomTypeWithRooms>,
        checkIn: Long,
        checkOut: Long
    ): List<RoomTypeWithRooms> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val checkInStr = dateFormat.format(Date(checkIn))
        val checkOutStr = dateFormat.format(Date(checkOut))

        return roomTypes.map { roomType ->
            try {
                // Gọi API lấy phòng trống
                // response bây giờ là List<AvailableRoom>
                val availableRoomsList = ApiClient.roomService.getAvailableRooms(
                    roomTypeId = roomType.id,
                    checkin = checkInStr,
                    checkout = checkOutStr
                )

                Log.d("HotelDetailVM", "API Response for ${roomType.name}: $availableRoomsList")

                val filteredRooms = roomType.rooms.filter { room ->
                    availableRoomsList.any { availableRoom ->
                        availableRoom.roomId == room.id
                    }
                }

                Log.d("HotelDetailVM", "${roomType.name}: ${filteredRooms.size}/${roomType.rooms.size} phòng trống")

                roomType.copy(rooms = filteredRooms)

            } catch (e: Exception) {
                Log.e("HotelDetailVM", "Error loading available rooms for ${roomType.name}: ${e.message}", e)
                roomType
            }
        }
    }
}
