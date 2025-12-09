package com.example.btl.api

import com.example.btl.model.RoomType
import retrofit2.Call
import retrofit2.http.*

interface RoomTypeService {

    // GET /admin/room-type - List All
    @GET("admin/room-type")
    suspend fun getAllRoomTypes(): List<RoomType>

    // POST /admin/room-type - Create Room Type
    @POST("admin/room-type")
    suspend fun createRoomType(@Body roomType: RoomType): RoomType

    // GET /admin/room-type/property/{property_id} - List By Property
    @GET("admin/room-type/property/{property_id}")
    suspend fun getRoomTypesByProperty(
        @Path("property_id") propertyId: Int
    ): List<RoomType>

    // PATCH /admin/room-type/{room_type_id} - Update Room Type
    @PATCH("admin/room-type/{room_type_id}")
    suspend fun updateRoomType(
        @Path("room_type_id") roomTypeId: Int,
        @Body roomType: RoomType
    ): RoomType

    // DELETE /admin/room-type/{room_type_id} - Delete Room Type
    @DELETE("admin/room-type/{room_type_id}")
    suspend fun deleteRoomType(@Path("room_type_id") roomTypeId: Int): Any
}
