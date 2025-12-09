package com.example.btl.model

data class Property(
    val id: Int,
    val name: String,
    val description: String?,
    val address: String,
    val is_active: Boolean,
    val checkin: String,   // Varchar trong DB -> String
    val checkout: String,
    val contact: String?,
    val latitude: Double,  // Double precision -> Double
    val longitude: Double,
    val image: String?
)