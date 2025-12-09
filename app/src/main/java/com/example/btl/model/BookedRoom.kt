package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class BookedRoom(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("booking_id")
    val booking_id: Int,

    @SerializedName("room_id")
    val room_id: Int
)
