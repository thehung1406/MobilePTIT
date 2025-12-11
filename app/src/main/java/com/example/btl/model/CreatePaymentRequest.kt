package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class CreatePaymentRequest(
    @SerializedName("booking_id")
    val bookingId: Int,

    @SerializedName("amount")
    val amount: Int,

    @SerializedName("payment_type")
    val paymentType: String = "string" // or some default
)
