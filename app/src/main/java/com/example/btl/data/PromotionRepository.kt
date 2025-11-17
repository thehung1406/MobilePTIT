package com.example.btl.data

import android.graphics.Color
import com.example.btl.Promotion
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PromotionRepository @Inject constructor() {
    fun getPromotions(): List<Promotion> {
        // In a real app, this would fetch data from a network or database.
        return listOf(
            Promotion("Ưu đãi mùa thu\nGiảm tới 50%", Color.parseColor("#D32F2F")),
            Promotion("Đi nhiều hơn\nGiảm đến 20%", Color.parseColor("#4CAF50"))
        )
    }
}
