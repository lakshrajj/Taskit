package com.ooolrs.taskit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.ooolrs.taskit.databinding.ActivityRewardHistoryBinding

class RewardHistory : AppCompatActivity() {
    private lateinit var binding: ActivityRewardHistoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}