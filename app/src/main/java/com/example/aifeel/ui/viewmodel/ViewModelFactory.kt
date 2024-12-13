package com.example.aifeel.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aifeel.data.repository.AuthRepository
import com.example.aifeel.data.repository.JournalRepository
import com.example.aifeel.data.retrofit.ApiService

class ViewModelFactory(
    private val authRepository: AuthRepository? = null,
    private val journalRepository: JournalRepository? = null,
    private val apiServiceML: ApiService? = null
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) && authRepository != null -> {
                AuthViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(JournalViewModel::class.java) && journalRepository != null -> {
                JournalViewModel(journalRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}

