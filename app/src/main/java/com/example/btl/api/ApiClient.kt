package com.example.btl.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "http://192.168.100.233:8000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    // Thêm các service mới
    val propertyService: PropertyService by lazy {
        retrofit.create(PropertyService::class.java)
    }

    val roomTypeService: RoomTypeService by lazy {
        retrofit.create(RoomTypeService::class.java)
    }

    val roomService: RoomService by lazy {
        retrofit.create(RoomService::class.java)
    }

    val roomSearchService: RoomSearchService by lazy {
        retrofit.create(RoomSearchService::class.java)
    }
}
