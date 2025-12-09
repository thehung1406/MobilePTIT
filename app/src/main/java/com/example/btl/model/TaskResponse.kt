package com.example.btl.model

import com.google.gson.annotations.SerializedName

// Response từ POST /booking (Celery Task)
data class TaskResponse(
    @SerializedName("message")
    val message: String,

    @SerializedName("task_id")
    val task_id: String,

    @SerializedName("status")
    val status: String // "queued", "processing", "completed", "failed"
)
