package com.example.btl.ui.profile

data class Review(
    val username: String,
    val rating: Float,
    val date: String,
    val reviewText: String,
    val hotelName: String,
    val hotelAddress: String
)
