package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("room_type_id")
    val room_type_id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("is_active")
    val is_active: Boolean = true,

    @SerializedName("image")
    val image: String?,

    @SerializedName("checkin")
    val checkin: String? = null,

    @SerializedName("checkout")
    val checkout: String? = null
)
