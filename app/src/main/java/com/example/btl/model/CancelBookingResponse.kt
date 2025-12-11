package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class CancelBookingResponse(
    @SerializedName("message")
    val message: String
)
