package com.example.btl.ui.hotel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl.api.ApiClient
import com.example.btl.model.Property
import com.example.btl.model.PropertySearchRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HotelSearchViewModel : ViewModel() {

    private val _searchResults = MutableStateFlow<List<Property>>(emptyList())
    val searchResults: StateFlow<List<Property>> = _searchResults.asStateFlow()

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

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

            Log.d("HotelSearchVM", "Searching with keyword: '$location'")

            try {
                // ✅ Gọi API search
                val response = ApiClient.propertyService.searchProperties(
                    PropertySearchRequest(keyword = location)
                )

                // ✅ Convert PropertySearchResult → Property
                val properties = response.results.map { result ->
                    Property(
                        id = result.id,
                        name = result.name,
                        address = result.address,
                        image = result.image,
                        description = result.description,
                        checkin = result.checkin,
                        checkout = result.checkout,
                        latitude = result.latitude,
                        longitude = result.longitude,
                        isActive = true,
                        contact = result.contact

                    )
                }

                _searchResults.value = properties
                _error.value = if (properties.isEmpty()) {
                    "Không tìm thấy khách sạn với từ khóa '$location'"
                } else {
                    ""
                }

                Log.d("HotelSearchVM", "Found ${properties.size} properties")

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
}
