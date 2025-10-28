package com.example.btl.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.btl.Promotion
import com.example.btl.data.PromotionRepository

class MainViewModel : ViewModel() {

    private val repository = PromotionRepository()

    private val _promotions = MutableLiveData<List<Promotion>>()
    val promotions: LiveData<List<Promotion>> = _promotions

    fun loadPromotions() {
        _promotions.value = repository.getPromotions()
    }
}
