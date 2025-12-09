package com.example.btl.ui.hotel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl.api.ApiClient
import com.example.btl.model.Property
import com.example.btl.model.RoomType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelDetailViewModel @Inject constructor() : ViewModel() {

    private val _propertyDetail = MutableStateFlow<Property?>(null)
    val propertyDetail: StateFlow<Property?> = _propertyDetail.asStateFlow()

    private val _roomTypes = MutableStateFlow<List<RoomType>>(emptyList())
    val roomTypes: StateFlow<List<RoomType>> = _roomTypes.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun loadPropertyDetail(propertyId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val property = ApiClient.propertyService.getProperty(propertyId)
                _propertyDetail.value = property
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Lỗi tải thông tin khách sạn: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadRoomTypes(propertyId: Int) {
        viewModelScope.launch {
            try {
                val roomTypes = ApiClient.roomTypeService.getRoomTypesByProperty(propertyId)
                _roomTypes.value = roomTypes.filter { it.is_active }
                _error.value = ""
            } catch (e: Exception) {
                _error.value = "Lỗi tải danh sách phòng: ${e.message}"
                _roomTypes.value = emptyList()
            }
        }
    }
}
