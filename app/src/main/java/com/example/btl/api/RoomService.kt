package com.example.btl.api

import com.example.btl.model.AvailableRoom
import retrofit2.http.*

interface RoomService {

    /**
     * GET /rooms/room-types/{room_type_id}/available-rooms
     * Lấy danh sách phòng available theo room_type_id và ngày
     * Response: List<AvailableRoom>
     */
    @GET("rooms/room-types/{room_type_id}/available-rooms")
    suspend fun getAvailableRooms(
        @Path("room_type_id") roomTypeId: Int,
        @Query("checkin") checkin: String,      // Format: "2025-12-11"
        @Query("checkout") checkout: String     // Format: "2025-12-12"
    ): List<AvailableRoom>
}
