package com.goldman.test.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.goldman.test.databinding.ActivitySplashBinding
import com.goldman.test.ui.home.MainActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({ redirectToNextScreen() },2000)
    }

    private fun redirectToNextScreen() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
}