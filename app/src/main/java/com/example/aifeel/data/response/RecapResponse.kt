package com.example.aifeel.data.response

data class WeeklyRecapResponse(
    val message: String,
    val recap: WeeklyRecap
)

data class WeeklyRecap(
    val weekStartDate: String,
    val weekEndDate: String,
    val dominantMoodCategory: String,
    val moodCounts: MoodCounts
)

data class MoodCounts(
    val moodCategoryCounts: Map<String, Int>
)

