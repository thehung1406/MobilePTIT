package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class BookingRequest(
    @SerializedName("room_ids")
    val roomIds: List<Int>,        // ✅ Danh sách ID phòng

    @SerializedName("checkin")
    val checkin: String,           // Format: "2025-12-13"

    @SerializedName("checkout")
    val checkout: String,          // Format: "2025-12-15"

    @SerializedName("num_guests")
    val numGuests: Int             // Số khách
)
