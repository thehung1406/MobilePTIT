package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class PropertySearchResponse(
    @SerializedName("results")
    val results: List<Property>
)

