package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("user_id")
    val userId: Int?,

    @SerializedName("id")
    val id: Int,

    @SerializedName("num_guests")
    val numGuests: Int,

    @SerializedName("selected_rooms")
    val selectedRooms: List<Int>?,

    @SerializedName("checkin")
    val checkin: String,

    @SerializedName("checkout")
    val checkout: String,

    @SerializedName("booking_date")
    val bookingDate: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("expires_at")
    val expiresAt: String?
)
