package com.example.btl.api

import com.example.btl.model.Property
import retrofit2.Call
import retrofit2.http.*

interface PropertyService {

    // GET /property - List Properties
    @GET("property")
    suspend fun getProperties(): List<Property>

    // POST /property - Create Property
    @POST("property")
    suspend fun createProperty(@Body property: Property): Property

    // GET /property/{prop_id} - Get Property
    @GET("property/{prop_id}")
    suspend fun getProperty(@Path("prop_id") propertyId: Int): Property

    // PATCH /property/{prop_id} - Update Property
    @PATCH("property/{prop_id}")
    suspend fun updateProperty(
        @Path("prop_id") propertyId: Int,
        @Body property: Property
    ): Property

    // DELETE /property/{prop_id} - Delete Property
    @DELETE("property/{prop_id}")
    suspend fun deleteProperty(@Path("prop_id") propertyId: Int): Any
}
