package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class BookingResponse(
    @SerializedName("id")
    val id: Int?,

    @SerializedName("booking_id")
    val bookingIdAlt: Int?,

    @SerializedName("selected_rooms")
    val selectedRooms: List<Int>?,
    
    // Support legacy/alternative format just in case
    @SerializedName("rooms") 
    val rooms: List<BookingRoom>?,

    @SerializedName("checkin")
    val checkin: String?,

    @SerializedName("checkout")
    val checkout: String?,

    @SerializedName("request_at")
    val requestAt: String?,

    @SerializedName("booking_date")
    val bookingDate: String?,

    @SerializedName("expires_at")
    val expiresAt: String?,

    @SerializedName("status")
    val status: String?
) {
    val bookingId: Int 
        get() = id ?: bookingIdAlt ?: 0

    // Helper to get displayable checkin/checkout
    val displayCheckin: String?
        get() = checkin ?: rooms?.firstOrNull()?.checkin
        
    val displayCheckout: String?
        get() = checkout ?: rooms?.firstOrNull()?.checkout
}

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
