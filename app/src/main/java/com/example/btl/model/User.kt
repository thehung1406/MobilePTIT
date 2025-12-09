package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("full_name")
    val full_name: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("is_active")
    val is_active: Boolean = true,

    @SerializedName("created_at")
    val created_at: String? = null
)
