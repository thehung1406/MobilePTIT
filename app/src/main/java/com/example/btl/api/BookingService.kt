package com.example.btl.api

import com.example.btl.model.BookingRequest
import com.example.btl.model.BookingResponse
import com.example.btl.model.CancelBookingResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface BookingService {

    /**
     * POST /booking
     * Tạo booking mới
     */
    @POST("booking")
    suspend fun createBooking(
        @Header("Authorization") token: String,
        @Body request: BookingRequest
    ): BookingResponse
    
    /**
     * GET /booking/my
     * Lấy danh sách booking của user
     */
    @GET("booking/my")
    suspend fun getMyBookings(
        @Header("Authorization") token: String
    ): List<BookingResponse>
    
    /**
     * POST /booking/{booking_id}/cancel
     * Hủy booking
     */
    @POST("booking/{booking_id}/cancel")
    suspend fun cancelBooking(
        @Header("Authorization") token: String,
        @Path("booking_id") bookingId: Int
    ): CancelBookingResponse
}
