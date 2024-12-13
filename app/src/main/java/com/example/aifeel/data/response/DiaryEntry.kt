package com.example.aifeel.data.response

data class DiaryEntry(
    val content: String,
    val date: String,
    val moodCategory: String,
    val predictedEmotion: String
)
