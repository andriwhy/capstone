package com.example.aifeel.ui

import android.os.Bundle
import android.text.Html
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aifeel.R

class ResultMoodActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result_mood)

        val moodCategory = intent.getStringExtra("MOOD_CATEGORY") ?: "Unknown Mood"
        val activityRecommendation = intent.getStringExtra("ACTIVITY_RECOMMENDATION") ?: "No Recommendation"


        findViewById<TextView>(R.id.subtitleTextView).text = "Mood Category: $moodCategory"

        val formattedActivityRecommendation = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Html.fromHtml(activityRecommendation, Html.FROM_HTML_MODE_LEGACY)
        } else {
            @Suppress("DEPRECATION")
            Html.fromHtml(activityRecommendation)
        }

        findViewById<TextView>(R.id.contentTextView).text = formattedActivityRecommendation

        val moodboosterIcon = findViewById<ImageView>(R.id.moodboosterIcon)

        when (moodCategory) {
            "Bad Mood" -> moodboosterIcon.setImageResource(R.drawable.ic_badmood)
            "Good Mood" -> moodboosterIcon.setImageResource(R.drawable.ic_goodmood)
            else -> moodboosterIcon.setImageResource(R.drawable.image_placeholder)
        }

        setupUI()
    }

    private fun setupUI() {
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }
    }
}
