package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class PropertyDetailResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("address")
    val address: String,

    @SerializedName("image")
    val image: String?,

    @SerializedName("latitude")
    val latitude: Double,

    @SerializedName("longitude")
    val longitude: Double,

    @SerializedName("checkin")
    val checkin: String,

    @SerializedName("checkout")
    val checkout: String,

    @SerializedName("contact")
    val contact: String?,

    @SerializedName("room_types")  // ✅ Khớp với API: room_types
    val roomTypes: List<RoomTypeWithRooms>,

    @SerializedName("reviews")
    val reviews: List<Any>? = null
) {
    // ✅ Helper để convert sang Property model cũ
    fun toProperty(): Property {
        return Property(
            id = id,
            name = name,
            description = description,
            address = address,
            image = image,
            latitude = latitude,
            longitude = longitude,
            checkin = checkin,
            checkout = checkout,
            contact = contact,
            isActive = true
        )
    }
}
