package com.example.btl.model

data class Room(
    val id: Int,
    val room_type_id: Int,
    val name: String,
    val image: String?,
    val is_active: Boolean
)