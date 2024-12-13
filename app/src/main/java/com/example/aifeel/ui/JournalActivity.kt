package com.example.aifeel.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.aifeel.R
import com.example.aifeel.data.repository.JournalRepository
import com.example.aifeel.data.response.DiaryRequest
import com.example.aifeel.data.retrofit.ApiConfig
import com.example.aifeel.ui.viewmodel.JournalViewModel
import com.example.aifeel.ui.viewmodel.ViewModelFactory
import com.google.android.material.button.MaterialButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class JournalActivity : AppCompatActivity() {

    private var userId: String? = null
    private var token: String? = null

    private val journalViewModel: JournalViewModel by viewModels {
        val apiService = ApiConfig.getApiService()
        val apiServiceML = ApiConfig.getApiServiceML()
        val journalRepository = JournalRepository(apiService, apiServiceML)
        ViewModelFactory(journalRepository = journalRepository)
    }

    companion object {
        const val EXTRA_TOKEN = "extra_token"
        const val EXTRA_USER_ID = "extra_user_id"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journal)

        token = intent.getStringExtra(EXTRA_TOKEN)
        userId = intent.getStringExtra(EXTRA_USER_ID)

        if (token.isNullOrEmpty() || userId.isNullOrEmpty()) {
            Toast.makeText(this, "Token atau User ID tidak ditemukan", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupUI()
        observeViewModel()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupUI() {
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }

        findViewById<TextView>(R.id.tv_title).text = "Bagaimana Perasaanmu?"
        findViewById<TextView>(R.id.tv_subtitle).text = "Ceritakan lebih banyak tentang hari mu!"

        findViewById<MaterialButton>(R.id.btn_continue).setOnClickListener {
            createDiary()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createDiary() {
        val storyContent = findViewById<EditText>(R.id.tv_content).text.toString()

        if (storyContent.isEmpty()) {
            Toast.makeText(this, "Tolong ceritakan sesuatu sebelum melanjutkan!", Toast.LENGTH_SHORT).show()
            return
        }

        val currentDate = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)

        val diaryRequest = DiaryRequest(
            userId = userId ?: "",
            date = currentDate,
            content = storyContent
        )

        token?.let {
            journalViewModel.createDiary(it, diaryRequest)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun observeViewModel() {
        journalViewModel.diaryResponseLiveData.observe(this, Observer { diaryResponse ->
            val intent = Intent(this, ResultMoodActivity::class.java).apply {
                putExtra("MOOD_CATEGORY", diaryResponse.moodCategory)
                putExtra("ACTIVITY_RECOMMENDATION", diaryResponse.activityRecommendation)
            }
            startActivity(intent)
            finish()
        })

        journalViewModel.errorLiveData.observe(this, Observer { errorMessage ->
            Toast.makeText(this, "Error: $errorMessage", Toast.LENGTH_SHORT).show()
        })
    }

}
