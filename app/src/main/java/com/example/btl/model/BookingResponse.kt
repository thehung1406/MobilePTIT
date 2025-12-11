package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class BookingResponse(
    @SerializedName("booking_id")
    val bookingId: Int?,

    @SerializedName("rooms")
    val rooms: List<BookingRoom>?,

    @SerializedName("request_at")
    val requestAt: String?,

    @SerializedName("expires_at")
    val expiresAt: String?,

    @SerializedName("status")
    val status: String?
)

data class BookingRoom(
    @SerializedName("room_id")
    val roomId: Int,

    @SerializedName("room_name")
    val roomName: String?,

    @SerializedName("checkin")
    val checkin: String?,

    @SerializedName("checkout")
    val checkout: String?
)
