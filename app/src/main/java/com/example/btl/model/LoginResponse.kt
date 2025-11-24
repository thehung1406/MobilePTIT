package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("otp_required")
    val otpRequired: Boolean,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("token_type")
    val tokenType: String,
    val role: String,
    @SerializedName("full_name")
    val fullName: String,
    val email: String
)