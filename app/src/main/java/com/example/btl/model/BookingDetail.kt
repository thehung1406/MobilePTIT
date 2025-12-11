package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class BookingDetail(
    @SerializedName("booking")
    val booking: Booking,

    @SerializedName("rooms")
    val rooms: List<RoomDetail>,

    @SerializedName("property")
    val property: Property?,

    @SerializedName("total_price")
    val totalPrice: Int
)

data class RoomDetail(
    @SerializedName("room")
    val room: Room,

    @SerializedName("room_type")
    val roomType: RoomType,

    @SerializedName("quantity")
    val quantity: Int = 1
)
