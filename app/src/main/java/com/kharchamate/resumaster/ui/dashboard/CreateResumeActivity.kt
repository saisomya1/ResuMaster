package com.kharchamate.resumaster.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kharchamate.resumaster.databinding.ActivityCreateResumeBinding

class CreateResumeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateResumeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateResumeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}
