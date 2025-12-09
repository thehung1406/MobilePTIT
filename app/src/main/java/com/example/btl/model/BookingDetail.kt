package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class BookingDetail(
    @SerializedName("id")
    val id: Int,

    @SerializedName("user_id")
    val user_id: Int,

    @SerializedName("booking_date")
    val booking_date: String,

    @SerializedName("checkin")
    val checkin: String,

    @SerializedName("checkout")
    val checkout: String,

    @SerializedName("num_guests")
    val num_guests: Int,

    @SerializedName("status")
    val status: String,

    @SerializedName("total_price")
    val total_price: Int,

    @SerializedName("property")
    val property: Property? = null,

    @SerializedName("rooms")
    val rooms: List<Room>? = null,

    @SerializedName("user")
    val user: User? = null,

    @SerializedName("booked_rooms")
    val booked_rooms: List<BookedRoom>? = null
)
