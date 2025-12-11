package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("image")
    val image: String?,

    @SerializedName("room_type_id")
    val roomTypeId: Int
)
