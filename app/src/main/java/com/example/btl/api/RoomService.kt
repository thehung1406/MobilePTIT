package com.example.btl.api

import com.example.btl.model.Room
import retrofit2.Call
import retrofit2.http.*

interface RoomService {

    // GET /room - List All Rooms
    @GET("room")
    suspend fun getAllRooms(): List<Room>

    // POST /room - Create Room
    @POST("room")
    suspend fun createRoom(@Body room: Room): Room

    // GET /room/property/{property_id} - List By Property
    @GET("room/property/{property_id}")
    suspend fun getRoomsByProperty(
        @Path("property_id") propertyId: Int
    ): List<Room>

    // PATCH /room/{room_id} - Update Room
    @PATCH("room/{room_id}")
    suspend fun updateRoom(
        @Path("room_id") roomId: Int,
        @Body room: Room
    ): Room

    // DELETE /room/{room_id} - Delete Room
    @DELETE("room/{room_id}")
    suspend fun deleteRoom(@Path("room_id") roomId: Int): Any
}
