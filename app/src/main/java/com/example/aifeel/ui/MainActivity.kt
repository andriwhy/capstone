package com.example.aifeel.ui


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.auth0.android.jwt.JWT
import com.example.aifeel.R
import com.example.aifeel.data.retrofit.ApiService
import com.example.aifeel.data.store.SessionPreference
import com.example.aifeel.data.retrofit.ApiConfig
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var sessionPreferences: SessionPreference
    private lateinit var apiService: ApiService

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: Initializing MainActivity")

        apiService = ApiConfig.getApiService()
        sessionPreferences = SessionPreference.getInstance(this)

        lifecycleScope.launch {
            Log.d(TAG, "onCreate: Launching checkTokenAndSetup coroutine")
            checkTokenAndSetup()
        }
    }

    private suspend fun checkTokenAndSetup() {
        Log.d(TAG, "checkTokenAndSetup: Starting token retrieval")

        val token = withContext(Dispatchers.IO) {
            Log.d(TAG, "checkTokenAndSetup: Attempting to get token from SessionPreferences")
            sessionPreferences.getToken().first().also {
                Log.d(TAG, "checkTokenAndSetup: Token retrieval completed")
            }
        }

        Log.d(TAG, "checkTokenAndSetup: Token value - ${if (token.isNullOrEmpty()) "EMPTY" else "NOT EMPTY"}")

        if (token.isNullOrEmpty()) {
            Log.w(TAG, "checkTokenAndSetup: Token is empty. Navigating to OpeningActivity.")
            navigateToOpening()
        } else {
            val userId = getUserIdFromToken(token)
            Log.d(TAG, "checkTokenAndSetup: User ID - $userId")

            Log.d(TAG, "checkTokenAndSetup: Token is valid. Setting up main content view.")
            setContentView(R.layout.activity_main)

            setupUI(token, userId ?: "default_user_id")
        }
    }


    private fun getUserIdFromToken(token: String?): String? {
        if (token == null) return null
        try {
            val jwt = JWT(token)
            return jwt.getClaim("userId").asString()
        } catch (e: Exception) {
            Log.e(TAG, "getUserIdFromToken: Error decoding token", e)
            return null
        }
    }

    private fun setupUI(token: String, userId: String) {
        Log.d(TAG, "setupUI: Configuring UI with token and userId")
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_smile -> {
                    Log.d(TAG, "setupUI: Smile navigation item selected")
                    true
                }
                R.id.nav_ai -> {
                    Log.d(TAG, "setupUI: AI navigation item selected")
                    navigateToJournal(token, userId)
                    true
                }
                R.id.nav_statistic -> {
                    Log.d(TAG, "setupUI: Setting navigation item selected")
                    navigateToStatisticActivity(token, userId)
                    true
                }
                else -> false
            }
        }
        findViewById<View>(R.id.statistic)?.setOnClickListener {
            navigateToStatisticActivity(token, userId)
        }

        findViewById<View>(R.id.journal)?.setOnClickListener {
            Log.d(TAG, "setupUI: Journal view clicked")
            navigateToJournal(token, userId)
        }

        findViewById<ImageView>(R.id.logout).setOnClickListener {
            Log.d(TAG, "setupUI: Logout button clicked")
            logoutUser()
        }
    }

    private fun navigateToStatisticActivity(token: String, userId: String) {
        Log.d(TAG, "navigateToStatisticActivity: Starting StatisticActivity with token: $token and userId: $userId")
        val intent = Intent(this, StatisticActivity::class.java).apply {
            putExtra("EXTRA_TOKEN", token)
            putExtra("EXTRA_USER_ID", userId)
        }
        startActivity(intent)
    }

    private fun navigateToJournal(token: String, userId: String) {
        Log.d(TAG, "navigateToJournal: Starting JournalActivity")
        val intent = Intent(this, JournalActivity::class.java).apply {
            putExtra(JournalActivity.EXTRA_TOKEN, token)
            putExtra(JournalActivity.EXTRA_USER_ID, userId)
        }
        startActivity(intent)
    }

    private fun navigateToOpening() {
        Log.d(TAG, "navigateToOpening: Starting OpeningActivity")
        val intent = Intent(this, OpeningActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun logoutUser() {
        Log.d(TAG, "logoutUser: Clearing session")
        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                sessionPreferences.clearSession()
            }
            navigateToOpening()
        }
    }
}
