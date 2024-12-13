package com.example.aifeel.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.aifeel.data.repository.JournalRepository
import com.example.aifeel.data.response.WeeklyRecap
import com.example.aifeel.data.retrofit.ApiConfig
import com.example.aifeel.databinding.ActivityStatisticBinding
import com.example.aifeel.ui.adapter.DiaryAdapter
import com.example.aifeel.ui.viewmodel.JournalViewModel
import com.example.aifeel.ui.viewmodel.ViewModelFactory
import android.content.Intent
import android.widget.ImageButton
import com.example.aifeel.R

class StatisticActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticBinding
    private lateinit var diaryAdapter: DiaryAdapter
    private val journalViewModel: JournalViewModel by viewModels {
        ViewModelFactory(
            journalRepository = JournalRepository(
                ApiConfig.getApiService(),
                ApiConfig.getApiServiceML()
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticBinding.inflate(layoutInflater)
        setContentView(binding.root)

        diaryAdapter = DiaryAdapter()

        val token = intent.getStringExtra("EXTRA_TOKEN") ?: ""
        val userId = intent.getStringExtra("EXTRA_USER_ID") ?: ""

        journalViewModel.fetchWeeklyRecap(userId, token)
        journalViewModel.weeklyRecap.observe(this) { recap ->
            recap?.let { displayWeeklyRecap(it) }
        }

        binding.btnViewHistory.setOnClickListener {
            val intent = Intent(this, HistoryDiaryActivity::class.java)
            intent.putExtra("EXTRA_USER_ID", userId)
            startActivity(intent)
        }

        setupUI()
    }

    private fun displayWeeklyRecap(recap: WeeklyRecap) {
        binding.tvWeekRange.text = "${recap.weekStartDate} - ${recap.weekEndDate}"
        binding.tvDominantMood.text = recap.dominantMoodCategory

        when (recap.dominantMoodCategory) {
            "Good Mood" -> {
                binding.ivDominantMood.setImageResource(R.drawable.ic_goodmood)
            }
            "Bad Mood" -> {
                binding.ivDominantMood.setImageResource(R.drawable.ic_badmood)
            }
            else -> {
                binding.ivDominantMood.setImageResource(R.drawable.normal_face)
            }
        }
    }

    private fun setupUI() {
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }
    }
}
