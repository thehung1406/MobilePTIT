package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class RoomType(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("room_type_id")
    val room_type_id: Int? = null, // Alias cho id nếu backend trả về khác

    @SerializedName("property_id")
    val property_id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Int,

    @SerializedName("max_occupancy")
    val max_occupancy: Int,

    @SerializedName("is_active")
    val is_active: Boolean = true
)
