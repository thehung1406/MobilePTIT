package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class Booking(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("user_id")
    val user_id: Int,

    @SerializedName("booking_date")
    val booking_date: String? = null, // Ngày tạo booking

    @SerializedName("checkin")
    val checkin: String? = null, // Ngày check-in

    @SerializedName("checkout")
    val checkout: String? = null, // Ngày check-out

    @SerializedName("num_guests")
    val num_guests: Int,

    @SerializedName("price")
    val price: Int? = null, // Tổng giá

    @SerializedName("status")
    val status: String = "pending", // "pending", "confirmed", "cancelled", "completed"

    @SerializedName("expires_at")
    val expires_at: String? = null, // Thời gian hết hạn

    @SerializedName("created_at")
    val created_at: String? = null,

    @SerializedName("updated_at")
    val updated_at: String? = null
)

// Enum cho status
enum class BookingStatus(val value: String) {
    PENDING("pending"),
    CONFIRMED("confirmed"),
    CANCELLED("cancelled"),
    COMPLETED("completed");

    companion object {
        fun fromString(value: String): BookingStatus {
            return values().find { it.value == value } ?: PENDING
        }
    }
}
