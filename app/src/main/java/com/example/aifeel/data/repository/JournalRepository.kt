package com.example.aifeel.data.repository

import android.util.Log
import com.example.aifeel.data.response.DiaryListResponse
import com.example.aifeel.data.response.DiaryRequest
import com.example.aifeel.data.response.DiaryResponse
import com.example.aifeel.data.response.UserResponse
import com.example.aifeel.data.response.WeeklyRecapResponse
import com.example.aifeel.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JournalRepository(
    private val apiService: ApiService,
    private val apiServiceML: ApiService,
) {
    fun createDiary(token: String, diaryRequest: DiaryRequest, callback: (DiaryResponse) -> Unit, errorCallback: (String) -> Unit) {
        val authHeader = "Bearer $token"
        apiServiceML.createDiary(authHeader, diaryRequest).enqueue(object : Callback<DiaryResponse> {
            override fun onResponse(call: Call<DiaryResponse>, response: Response<DiaryResponse>) {
                if (response.isSuccessful && response.body() != null) {
                    callback(response.body()!!)
                } else {
                    errorCallback("Gagal membuat diary, ${response.message()}")
                }
            }

            override fun onFailure(call: Call<DiaryResponse>, t: Throwable) {
                errorCallback("Error: ${t.message}")
            }
        })
    }

    fun getDiaries(userId: String, callback: (List<DiaryResponse>) -> Unit, errorCallback: (String) -> Unit) {
        apiService.getDiaries(userId).enqueue(object : Callback<DiaryListResponse> {
            override fun onResponse(call: Call<DiaryListResponse>, response: Response<DiaryListResponse>) {
                if (response.isSuccessful) {
                    val diaries = response.body()?.diaries
                    if (diaries != null) {
                        callback(diaries)
                    } else {
                        errorCallback("No diaries found.")
                    }
                } else {
                    errorCallback("Failed to fetch diaries. Error code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<DiaryListResponse>, t: Throwable) {
                errorCallback("Error: ${t.message}")
            }
        })
    }

    suspend fun getWeeklyRecap(userId: String, token: String): WeeklyRecapResponse? {
        return withContext(Dispatchers.IO) {
            val response = apiService.getWeeklyRecap(userId, "Bearer $token").execute()
            if (response.isSuccessful) response.body() else null
        }
    }

    fun updateUser(userId: String, token: String, newValue: String) {
        val updateBody = mapOf("value" to newValue)
        val authToken = "Bearer $token"
        apiService.updateUser(userId, authToken, updateBody).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                } else {
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
            }
        })
    }

}
