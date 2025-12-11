package com.example.btl.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    // 10.0.2.2 là địa chỉ localhost của máy tính khi truy cập từ Android Emulator
    private const val BASE_URL = "http://192.168.0.131:8000/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

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

    val bookingService: BookingService by lazy {
        retrofit.create(BookingService::class.java)
    }
    
    val paymentService: PaymentService by lazy {
        retrofit.create(PaymentService::class.java)
    }
}
