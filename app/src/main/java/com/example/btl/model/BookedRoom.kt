package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class BookedRoom(
    @SerializedName("id")
    val id: Int,

    @SerializedName("booking_id")
    val bookingId: Int,

    @SerializedName("room_id")
    val roomId: Int,

    @SerializedName("checkin")
    val checkin: String,

    @SerializedName("checkout")
    val checkout: String
)
