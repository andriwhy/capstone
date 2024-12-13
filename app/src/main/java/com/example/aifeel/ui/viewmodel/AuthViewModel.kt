package com.example.aifeel.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aifeel.ui.utils.Result
import com.example.aifeel.data.repository.AuthRepository
import com.example.aifeel.data.response.LoginResponse
import com.example.aifeel.data.response.RegisterResponse
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val _registerResult = MutableLiveData<Result<RegisterResponse>>()
    val registerResult: LiveData<Result<RegisterResponse>> = _registerResult

    fun login(email: String, password: String) {
        _loginResult.value = Result.Loading
        viewModelScope.launch {
            try {
                val response = authRepository.login(email, password)
                _loginResult.value = response
            } catch (e: Exception) {
                _loginResult.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        _registerResult.value = Result.Loading
        viewModelScope.launch {
            try {
                val response = authRepository.register(name, email, password)
                _registerResult.value = response
            } catch (e: Exception) {
                _registerResult.value = Result.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun getToken(): LiveData<String?> {
        val tokenLiveData = MutableLiveData<String?>()
        viewModelScope.launch {
            try {
                val token = authRepository.getToken()
                tokenLiveData.postValue(token)
            } catch (e: Exception) {
                tokenLiveData.postValue(null)
            }
        }
        return tokenLiveData
    }

    fun saveToken(token: String) {
        viewModelScope.launch {
            try {
                authRepository.saveToken(token)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Failed to save token: ${e.message}")

            }
        }
    }
}
