package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class UserProfile(
    val email: String,
    @SerializedName("full_name")
    val fullName: String,
    val phone: String,
    val role: String,
    @SerializedName("property_id")
    val propertyId: Int?
)
