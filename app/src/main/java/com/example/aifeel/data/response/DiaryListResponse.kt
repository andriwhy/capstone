package com.example.aifeel.data.response

data class DiaryListResponse(
    val userId: String,
    val diaries: List<DiaryResponse>
)
