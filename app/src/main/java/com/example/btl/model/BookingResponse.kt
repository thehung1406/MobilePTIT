package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class BookingResponse(
    @SerializedName("booking")
    val booking: Booking,

    @SerializedName("booked_rooms")
    val booked_rooms: List<BookedRoom>,

    @SerializedName("total_price")
    val total_price: Int,

    @SerializedName("property")
    val property: Property? = null,

    @SerializedName("rooms")
    val rooms: List<Room>? = null
)
