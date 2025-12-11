package com.example.btl.ui.hotel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl.api.ApiClient
import com.example.btl.model.Property
import com.example.btl.model.RoomTypeWithRooms
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HotelDetailViewModel : ViewModel() {

    // ✅ Giữ nguyên interface cũ
    private val _property = MutableStateFlow<Property?>(null)
    val property: StateFlow<Property?> = _property.asStateFlow()

    private val _roomTypes = MutableStateFlow<List<RoomTypeWithRooms>>(emptyList())
    val roomTypes: StateFlow<List<RoomTypeWithRooms>> = _roomTypes.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ✅ Chỉ sửa logic bên trong
    fun loadPropertyDetail(propertyId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Gọi API mới
                val propertyDetail = ApiClient.propertyService.getPropertyDetail(propertyId)

                // Tách ra giống như cũ
                _property.value = propertyDetail.property
                _roomTypes.value = propertyDetail.roomTypes

                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Lỗi tải thông tin khách sạn: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
