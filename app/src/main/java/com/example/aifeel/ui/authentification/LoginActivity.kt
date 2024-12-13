package com.example.aifeel.ui.authentification

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aifeel.R
import com.example.aifeel.ui.utils.Result
import com.example.aifeel.ui.viewmodel.AuthViewModel
import com.example.aifeel.data.repository.AuthRepository
import com.example.aifeel.data.retrofit.ApiConfig
import com.example.aifeel.data.store.SessionPreference
import com.example.aifeel.databinding.ActivityLoginBinding
import com.example.aifeel.ui.MainActivity
import com.example.aifeel.ui.viewmodel.ViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(
            authRepository = AuthRepository.getInstance(
                ApiConfig.getApiService(),
                SessionPreference.getInstance(this)
            )
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        var isPasswordVisible = false

        binding.loginButton.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.password.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                hideKeyboard()
                authViewModel.login(email, password)
            } else {
                Toast.makeText(this, "All fields must not be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.footerText.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

        binding.visiblePass.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.password.inputType = android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.visiblePass.setImageResource(R.drawable.invincible)
            } else {
                binding.password.inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.visiblePass.setImageResource(R.drawable.visible)
            }
            binding.password.text?.let { it1 -> binding.password.setSelection(it1.length) }
        }
    }

    private fun hideKeyboard() {
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        val currentFocusView = currentFocus
        if (currentFocusView != null && !isFinishing && !isDestroyed) {
            inputMethodManager.hideSoftInputFromWindow(currentFocusView.windowToken, 0)
        }
    }

    private fun observeViewModel() {
        authViewModel.loginResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.loadingLayer.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loadingLayer.visibility = View.GONE

                    val token = result.data.token
                    saveTokenToSession(token)

                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                }
                is Result.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.loadingLayer.visibility = View.GONE
                    Toast.makeText(this, "Error: ${result.errorMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveTokenToSession(token: String) {
        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    authViewModel.saveToken(token)
                }
                android.util.Log.d("LoginActivity", "Token saved successfully: $token")
            } catch (e: Exception) {
                android.util.Log.e("LoginActivity", "Failed to save token: ${e.message}")
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
