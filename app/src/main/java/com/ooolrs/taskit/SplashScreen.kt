package com.ooolrs.taskit

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
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

        val animDrawable = binding.launchxml.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(3000)
        animDrawable.start()

        val anim = AnimationUtils.loadAnimation(this,R.anim.stripe_anim)
        binding.stripe.startAnimation(anim)

        if(auth.currentUser!=null){
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this,HomeActivity::class.java)
                startActivity(intent)

            }, 1500)
        }else{
            Handler(Looper.getMainLooper()).postDelayed({
                val intent = Intent(this,LoginActivity::class.java)
                startActivity(intent)

            }, 1500)
        }




    }
}