package com.example.btl.ui.hotel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl.api.ApiClient
import com.example.btl.model.Property
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelSearchViewModel @Inject constructor() : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Property>>(emptyList())
    val searchResults: StateFlow<List<Property>> = _searchResults.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    // ✅ THÊM: Load tất cả properties
    fun loadAllProperties() {
        viewModelScope.launch {
            _isLoading.value = true
            _hasSearched.value = true
            Log.d("HotelSearchVM", "Loading all properties...")

            try {
                val allProperties = ApiClient.propertyService.getProperties()
                val activeProperties = allProperties.filter { it.is_active }

                _searchResults.value = activeProperties
                _error.value = ""

                Log.d("HotelSearchVM", "Loaded ${activeProperties.size} properties")
            } catch (e: Exception) {
                Log.e("HotelSearchVM", "Error loading properties: ${e.message}")
                _error.value = "Lỗi tải dữ liệu: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ✅ SỬA: Cho phép search không cần location
    fun searchHotels(
        location: String,
        checkInDate: Long,
        checkOutDate: Long,
        guests: Int,
        rooms: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _hasSearched.value = true
            Log.d("HotelSearchVM", "Searching with location: '$location'")

            try {
                val allProperties = ApiClient.propertyService.getProperties()

                // ✅ Nếu không có location, hiển thị tất cả
                val filteredProperties = if (location.isEmpty()) {
                    allProperties.filter { it.is_active }
                } else {
                    allProperties.filter { property ->
                        property.is_active &&
                                (property.address.contains(location, ignoreCase = true) ||
                                        property.name.contains(location, ignoreCase = true))
                    }
                }

                _searchResults.value = filteredProperties
                _error.value = if (filteredProperties.isEmpty() && location.isNotEmpty()) {
                    "Không tìm thấy khách sạn tại $location"
                } else {
                    ""
                }

                Log.d("HotelSearchVM", "Found ${filteredProperties.size} properties")
            } catch (e: Exception) {
                Log.e("HotelSearchVM", "Search error: ${e.message}", e)
                _error.value = "Lỗi tìm kiếm: ${e.message}"
                _searchResults.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearResults() {
        _searchResults.value = emptyList()
        _hasSearched.value = false
        _error.value = ""
    }

    fun filterByPriceRange(minPrice: Int, maxPrice: Int) {
        // TODO: Implement price range filtering
    }

    fun sortByPrice(ascending: Boolean = true) {
        val currentList = _searchResults.value
        _searchResults.value = if (ascending) {
            currentList.sortedBy { it.name }
        } else {
            currentList.sortedByDescending { it.name }
        }
    }
}
