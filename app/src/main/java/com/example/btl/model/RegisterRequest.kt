package com.example.btl.model

data class RegisterRequest(
    val email: String,
    val full_name: String,
    val phone: String,
    val password: String
)