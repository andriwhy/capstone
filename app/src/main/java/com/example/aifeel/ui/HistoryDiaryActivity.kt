package com.example.aifeel.ui

import android.os.Bundle
import android.widget.Toast
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.aifeel.databinding.ActivityHistoryDiaryBinding
import com.example.aifeel.ui.adapter.DiaryAdapter
import com.example.aifeel.ui.viewmodel.JournalViewModel
import com.example.aifeel.data.repository.JournalRepository
import androidx.lifecycle.ViewModelProvider
import com.example.aifeel.R
import com.example.aifeel.data.retrofit.ApiConfig
import com.example.aifeel.ui.viewmodel.ViewModelFactory

class HistoryDiaryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryDiaryBinding
    private lateinit var journalViewModel: JournalViewModel
    private lateinit var diaryAdapter: DiaryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryDiaryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userId = intent.getStringExtra("EXTRA_USER_ID") ?: return

        val apiServiceML = ApiConfig.getApiServiceML()
        val journalRepository = JournalRepository(apiServiceML, apiServiceML)

        journalViewModel = ViewModelProvider(
            this,
            ViewModelFactory(journalRepository = journalRepository)
        ).get(JournalViewModel::class.java)

        setupRecyclerView()
        observeViewModel()
        journalViewModel.getDiaries(userId)

        setupUI()
    }

    private fun setupRecyclerView() {
        diaryAdapter = DiaryAdapter()
        binding.rvDiaries.apply {
            layoutManager = LinearLayoutManager(this@HistoryDiaryActivity)
            adapter = diaryAdapter
        }
    }

    private fun observeViewModel() {
        journalViewModel.diaryListLiveData.observe(this) { diaries ->
            if (diaries != null) {
                diaryAdapter.submitList(diaries)
            } else {
                Toast.makeText(this, "No diaries found", Toast.LENGTH_SHORT).show()
            }
        }

        journalViewModel.errorLiveData.observe(this) { errorMessage ->
            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupUI() {
        findViewById<ImageButton>(R.id.btn_back).setOnClickListener {
            onBackPressed()
        }
    }
}
