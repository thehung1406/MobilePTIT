package com.example.btl.api

import com.example.btl.model.*
import retrofit2.http.*

interface BookingService {

    // POST /booking - Tạo booking (trả về TaskResponse)
    @POST("booking")
    suspend fun createBooking(@Body bookingRequest: BookingRequest): TaskResponse  // ✅ Đổi từ BookingResponse

    // GET /booking - Lấy danh sách bookings
    @GET("booking")
    suspend fun getAllBookings(): List<Booking>

    // GET /booking/{booking_id}
    @GET("booking/{booking_id}")
    suspend fun getBookingById(@Path("booking_id") bookingId: Int): Booking

    // PATCH /booking/{booking_id}
    @PATCH("booking/{booking_id}")
    suspend fun updateBookingStatus(
        @Path("booking_id") bookingId: Int,
        @Body status: Map<String, String>
    ): Booking

    // DELETE /booking/{booking_id}
    @DELETE("booking/{booking_id}")
    suspend fun deleteBooking(@Path("booking_id") bookingId: Int): Map<String, String>
}
