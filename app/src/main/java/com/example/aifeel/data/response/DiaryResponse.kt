package com.example.aifeel.data.response

data class DiaryResponse(
    val diaryId: String,
    val content: String,
    val createdAt: String,
    val date: String,
    val moodCategory: String,
    val activityRecommendation :String,
    val predictedEmotion: String,
    val userId: String
)


