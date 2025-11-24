package com.example.btl.api

import com.example.btl.model.LoginResponse
import com.example.btl.model.RegisterRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthService {
    @POST("auth/register")
    fun register(@Body registerRequest: RegisterRequest): Call<Void>

    @FormUrlEncoded
    @POST("auth/login")
    fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password",
        @Field("scope") scope: String = "",
        @Field("client_id") clientId: String? = null,
        @Field("client_secret") clientSecret: String? = null
    ): Call<LoginResponse>
}