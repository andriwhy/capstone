package com.example.aifeel.ui.authentification

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.aifeel.R
import com.example.aifeel.ui.viewmodel.ViewModelFactory
import com.example.aifeel.data.repository.AuthRepository
import com.example.aifeel.data.retrofit.ApiConfig
import com.example.aifeel.data.store.SessionPreference
import com.example.aifeel.databinding.ActivityRegisterBinding
import com.example.aifeel.ui.utils.Result
import com.example.aifeel.ui.viewmodel.AuthViewModel

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(
            authRepository = AuthRepository.getInstance(
                ApiConfig.getApiService(),
                SessionPreference.getInstance(this)
            )
        )
    }

    private lateinit var edRegisterPassword: EditText
    private lateinit var edRegisterConfirmPassword: EditText
    private lateinit var visiblePass: ImageView
    private lateinit var confirmVisiblePass: ImageView

    private var isPasswordVisible = false
    private var isConfirmPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        edRegisterPassword = binding.edRegisterPassword
        edRegisterConfirmPassword = binding.edRegisterConfPassword
        visiblePass = binding.visiblePass
        confirmVisiblePass = binding.ConfirmvisiblePass

        setupListeners()
        observeViewModel()
    }

    private fun setupListeners() {
        binding.RegisterButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val confirmPassword = binding.edRegisterConfPassword.text.toString()
            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (password == confirmPassword) {
                    authViewModel.register(name, email, password)
                } else {
                    Toast.makeText(this, "Password do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "All field must not be empty", Toast.LENGTH_SHORT).show()
            }
        }

        binding.footerText.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        // Menangani klik pada icon password
        visiblePass.setOnClickListener { togglePasswordVisibility() }
        confirmVisiblePass.setOnClickListener { toggleConfirmPasswordVisibility() }
    }

    // Fungsi untuk menampilkan atau menyembunyikan password pertama
    private fun togglePasswordVisibility() {
        if (isPasswordVisible) {
            edRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            visiblePass.setImageResource(R.drawable.visible)
        } else {
            edRegisterPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            visiblePass.setImageResource(R.drawable.invincible)
        }
        isPasswordVisible = !isPasswordVisible
        edRegisterPassword.setSelection(edRegisterPassword.text.length)
    }

    private fun toggleConfirmPasswordVisibility() {
        if (isConfirmPasswordVisible) {
            edRegisterConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            confirmVisiblePass.setImageResource(R.drawable.visible)
        } else {
            edRegisterConfirmPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            confirmVisiblePass.setImageResource(R.drawable.invincible) // Ganti dengan ikon terlihat
        }
        isConfirmPasswordVisible = !isConfirmPasswordVisible
        edRegisterConfirmPassword.setSelection(edRegisterConfirmPassword.text.length) // Pindahkan kursor ke akhir
    }

    private fun observeViewModel() {
        authViewModel.registerResult.observe(this) { result ->
            when (result) {
                is Result.Loading -> {
                    // Handle loading state
                }
                is Result.Success -> {
                    Toast.makeText(this, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                is Result.Error -> {
                    Toast.makeText(this, "Error: ${result.errorMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
