package com.example.aifeel.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aifeel.data.repository.JournalRepository
import com.example.aifeel.data.response.DiaryRequest
import com.example.aifeel.data.response.DiaryResponse
import com.example.aifeel.data.response.WeeklyRecap
import kotlinx.coroutines.launch

class JournalViewModel(
    private val journalRepository: JournalRepository
) : ViewModel() {

    private val _weeklyRecap = MutableLiveData<WeeklyRecap>()
    val weeklyRecap: LiveData<WeeklyRecap> get() = _weeklyRecap

    val diaryResponseLiveData = MutableLiveData<DiaryResponse>()
    val diaryListLiveData = MutableLiveData<List<DiaryResponse>>()
    val errorLiveData = MutableLiveData<String>()

    fun createDiary(token: String, diaryRequest: DiaryRequest) {
        journalRepository.createDiary(token, diaryRequest, { diaryResponse ->
            diaryResponseLiveData.postValue(diaryResponse)
        }, { errorMessage ->
            errorLiveData.postValue(errorMessage)
        })
    }

    fun getDiaries(userId: String) {
        journalRepository.getDiaries(userId, { diaries ->
            diaryListLiveData.postValue(diaries)
        }, { errorMessage ->
            errorLiveData.postValue(errorMessage)
        })
    }

    fun fetchWeeklyRecap(userId: String, token: String) {
        viewModelScope.launch {
            val response = journalRepository.getWeeklyRecap(userId, token)
            response?.recap?.let { recap ->
                _weeklyRecap.postValue(recap)
            }
        }
    }

}

