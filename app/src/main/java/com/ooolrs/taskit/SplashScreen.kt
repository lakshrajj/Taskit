package com.ooolrs.taskit

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.ScaleAnimation
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.ooolrs.taskit.databinding.ActivitySplashScreenBinding

class SplashScreen : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        configureAnimation()
        handleUserAuthentication()
    }

    private fun configureAnimation() {
        val animDrawable = binding.launchxml.background as AnimationDrawable
        with(animDrawable) {
            setEnterFadeDuration(10)
            setExitFadeDuration(3000)
            start()
        }

        val anim = AnimationUtils.loadAnimation(this, R.anim.stripe_anim)
        binding.stripe.startAnimation(anim)
        applyBreathingAnimation()
    }

    private fun applyBreathingAnimation() {
        val scaleAnim = ScaleAnimation(
            1.0f, 1.2f, 1.0f, 1.2f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        scaleAnim.duration = 2000
        scaleAnim.repeatMode = Animation.REVERSE
        scaleAnim.repeatCount = Animation.INFINITE
        binding.imageView.startAnimation(scaleAnim)
    }

    private fun handleUserAuthentication() {
        val delayMillis = 2000L
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = if (auth.currentUser != null) {
                Intent(this, HomeActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(intent)
        }, delayMillis)
    }
}
