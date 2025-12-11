package com.example.btl.model

import com.google.gson.annotations.SerializedName

data class PropertySearchRequest(
    @SerializedName("keyword")
    val keyword: String
)
