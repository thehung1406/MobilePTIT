package com.example.btl.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
<<<<<<< HEAD
    private const val BASE_URL = "http://192.168.0.104:8000/"
=======
    private const val BASE_URL = "http://10.24.10.9:8000/"
>>>>>>> aebe3ad516c55f56ac22ea025b397ba5d7360a59

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }
}