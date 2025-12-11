package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class Property(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("address")
    val address: String,

    @SerializedName("latitude")
    val latitude: Double?,

    @SerializedName("longitude")
    val longitude: Double?,

    @SerializedName("image")
    val image: String?,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("checkin")
    val checkin: String,

    @SerializedName("checkout")
    val checkout: String,

    @SerializedName("contact")
    val contact: String?
)
