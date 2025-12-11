package com.example.btl.api

import com.example.btl.model.*
import retrofit2.http.*

interface BookingService {

    /**
     * POST /booking
     * Tạo booking mới
     * Body: {
     *   "room_ids": [1, 2],
     *   "checkin": "2025-12-13",
     *   "checkout": "2025-12-15",
     *   "num_guests": 2
     * }
     */
    @POST("booking")
    suspend fun createBooking(
        @Header("Authorization") token: String,
        @Body request: BookingRequest
    ): BookingResponse

    /**
     * GET /booking/my
     * Lấy danh sách booking của user hiện tại
     * Requires: Authorization header with Bearer token
     */
    @GET("booking/my")
    suspend fun getMyBookings(
        @Header("Authorization") token: String
    ): List<Booking>

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
