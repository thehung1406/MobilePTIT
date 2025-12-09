package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class BookingRequest(
    @SerializedName("room_ids")
    val room_ids: List<Int>,

    @SerializedName("checkin")
    val checkin: String, // Format: "2025-12-09"

    @SerializedName("checkout")
    val checkout: String, // Format: "2025-12-09"

    @SerializedName("num_guests")
    val num_guests: Int,

    @SerializedName("price")
    val price: Int
)
