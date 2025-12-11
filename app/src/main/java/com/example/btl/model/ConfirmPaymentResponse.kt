package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class ConfirmPaymentResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("booking_id")
    val bookingId: Int,

    @SerializedName("status")
    val status: String
)
