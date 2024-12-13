package com.example.aifeel.data.repository

import android.util.Log
import com.example.aifeel.data.response.LoginResponse
import com.example.aifeel.data.response.RegisterResponse
import com.example.aifeel.data.retrofit.ApiService
import com.example.aifeel.data.store.SessionPreference
import com.example.aifeel.ui.utils.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Call

class AuthRepository(
    private val apiService: ApiService,
    private val sessionPreference: SessionPreference

) {

    companion object {
        @Volatile
        private var instance: AuthRepository? = null

        fun getInstance(
            apiService: ApiService,
            sessionPreference: SessionPreference
        ): AuthRepository {
            return instance ?: synchronized(this) {
                instance ?: AuthRepository(apiService, sessionPreference).also { instance = it }
            }
        }
    }

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = apiService.login(email, password)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun register(name: String, email: String, password: String): Result<RegisterResponse> {
        return try {
            val response = apiService.register(name, email, password)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun saveToken(token: String) {
        Log.d("AuthRepository", "Saving token: $token")
        sessionPreference.saveToken(token)
    }

    suspend fun getToken(): String? {
        val token = sessionPreference.getToken().first()
        Log.d("AuthRepository", "Retrieved token: $token")
        return token
    }
}

