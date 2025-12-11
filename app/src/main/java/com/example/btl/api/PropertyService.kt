package com.example.btl.api

import com.example.btl.model.Property
import com.example.btl.model.PropertyDetailResponse
import com.example.btl.model.PropertySearchRequest
import com.example.btl.model.PropertySearchResponse
import retrofit2.http.*

interface PropertyService {

    @GET("properties")
    suspend fun getAllProperties(): List<Property>

    @GET("properties/{property_id}")
    suspend fun getPropertyDetail(
        @Path("property_id") propertyId: Int
    ): PropertyDetailResponse

    @POST("search/property")
    suspend fun searchProperties(
        @Body request: PropertySearchRequest
    ): PropertySearchResponse

    @POST("properties")
    suspend fun createProperty(
        @Header("Authorization") token: String,
        @Body property: Property
    ): Property

    @PUT("properties/{property_id}")
    suspend fun updateProperty(
        @Header("Authorization") token: String,
        @Path("property_id") propertyId: Int,
        @Body property: Property
    ): Property

    @DELETE("properties/{property_id}")
    suspend fun deleteProperty(
        @Header("Authorization") token: String,
        @Path("property_id") propertyId: Int
    ): Map<String, Any>

}
