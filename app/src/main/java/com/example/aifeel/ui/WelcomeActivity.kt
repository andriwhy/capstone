package com.example.aifeel.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.aifeel.R
import com.example.aifeel.data.store.SessionPreference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WelcomeActivity : AppCompatActivity() {
    private lateinit var sessionPreferences: SessionPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        sessionPreferences = SessionPreference.getInstance(this)

        val imageView = findViewById<ImageView>(R.id.logoImageView)

        val fadeInAnimation = AlphaAnimation(0f, 1f).apply {
            duration = 1500
            fillAfter = true
        }

        val fadeOutAnimation = AlphaAnimation(1f, 0f).apply {
            duration = 1500
            fillAfter = true
            startOffset = 2000
        }

        imageView.startAnimation(fadeInAnimation)

        fadeInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                imageView.startAnimation(fadeOutAnimation)

                fadeOutAnimation.setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation?) {}

                    override fun onAnimationEnd(animation: Animation?) {
                        navigateBasedOnToken()
                    }

                    override fun onAnimationRepeat(animation: Animation?) {}
                })
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun navigateBasedOnToken() {
        // Gunakan lifecycleScope untuk melakukan coroutine di background thread
        lifecycleScope.launch {
            // Ambil token di background thread
            val token = withContext(Dispatchers.IO) {
                sessionPreferences.getToken().first()
            }

            // Navigasi berdasarkan token
            if (token.isNullOrEmpty()) {
                startActivity(Intent(this@WelcomeActivity, OpeningActivity::class.java))
            } else {
                startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
            }
            finish()
        }
    }
}
