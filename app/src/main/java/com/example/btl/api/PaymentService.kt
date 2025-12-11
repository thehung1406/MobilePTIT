package com.example.btl.api

import com.example.btl.model.CreatePaymentRequest
import com.example.btl.model.CreatePaymentResponse
import com.example.btl.model.ConfirmPaymentResponse
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentService {

    /**
     * POST /payment
     * Create a new payment intent.
     */
    @POST("payment")
    suspend fun createPayment(
        @Header("Authorization") token: String,
        @Body request: CreatePaymentRequest
    ): CreatePaymentResponse

    /**
     * POST /payment/{payment_id}/confirm
     * Confirm a payment.
     */
    @POST("payment/{payment_id}/confirm")
    suspend fun confirmPayment(
        @Header("Authorization") token: String,
        @Path("payment_id") paymentId: Int
    ): ConfirmPaymentResponse
}
