package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class RoomType(
    @SerializedName("id")
    val id: Int,

    @SerializedName("property_id")
    val propertyId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Int,

    @SerializedName("max_occupancy")
    val maxOccupancy: Int,

    @SerializedName("is_active")
    val isActive: Boolean
)
