package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class AvailableRoomsResponse(
    @SerializedName("room_type_id")
    val roomTypeId: Int,

    @SerializedName("room_type_name")
    val roomTypeName: String,

    @SerializedName("available_rooms")
    val availableRooms: List<AvailableRoom>
)

data class AvailableRoom(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("image")
    val image: String? = null,

    @SerializedName("is_active")
    val isActive: Boolean = true,

    @SerializedName("room_type_id")
    val roomTypeId: Int = 0
) {
    // Compatibility helpers
    val roomId: Int get() = id
    val roomName: String get() = name
}
