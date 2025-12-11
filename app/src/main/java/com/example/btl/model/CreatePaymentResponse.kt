package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class CreatePaymentResponse(
    @SerializedName("payment_id")
    val paymentId: Int,

    @SerializedName("booking_id")
    val bookingId: Int,

    @SerializedName("payment_code")
    val paymentCode: String,

    @SerializedName("amount")
    val amount: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("status")
    val status: String
)
