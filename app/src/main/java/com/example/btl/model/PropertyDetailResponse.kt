package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class PropertyDetailResponse(
    @SerializedName("property")
    val property: Property,

    @SerializedName("room_types")
    val roomTypes: List<RoomTypeWithRooms>
)

data class RoomTypeWithRooms(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("price")
    val price: Int,

    @SerializedName("max_occupancy")
    val maxOccupancy: Int,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("rooms")
    val rooms: List<RoomInfo>
)

data class RoomInfo(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("image")
    val image: String?,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("room_type_id")
    val roomTypeId: Int
)
