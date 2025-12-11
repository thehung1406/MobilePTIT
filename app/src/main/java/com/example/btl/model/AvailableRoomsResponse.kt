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
    @SerializedName("room_id")
    val roomId: Int,

    @SerializedName("room_name")
    val roomName: String
)
