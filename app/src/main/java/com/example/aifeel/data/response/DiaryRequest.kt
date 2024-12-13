package com.example.aifeel.data.response

data class DiaryRequest(
    val userId: String,
    val date: String,
    val content: String
)