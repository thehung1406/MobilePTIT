package com.example.btl.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl.api.ApiClient
import com.example.btl.model.Property
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor() : ViewModel() {

    private val _properties = MutableLiveData<List<Property>>()
    val properties: LiveData<List<Property>> = _properties

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun loadProperties() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val propertyList = ApiClient.propertyService.getAllProperties()
                _properties.value = propertyList
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Không thể tải danh sách địa điểm: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
