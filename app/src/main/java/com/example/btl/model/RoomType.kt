package com.example.btl.model

data class RoomType(
    val id: Int,
    val property_id: Int,
    val name: String,
    val price: Int,        // Integer -> Int (hoặc Long nếu giá trị lớn)
    val max_occupancy: Int,
    val is_active: Boolean
)