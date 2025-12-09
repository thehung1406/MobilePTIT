package com.example.btl.api

import com.example.btl.model.RoomType
import retrofit2.http.GET
import retrofit2.http.Path

interface RoomSearchService {

    // GET /rooms/by-property/{property_id} - Get Room Types By Property
    @GET("rooms/by-property/{property_id}")
    suspend fun getRoomTypesByProperty(
        @Path("property_id") propertyId: Int
    ): List<RoomType>
}
