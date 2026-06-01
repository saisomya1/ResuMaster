package com.kharchamate.resumaster.ui.dashboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kharchamate.resumaster.databinding.ActivityTemplateBinding
import com.kharchamate.resumaster.util.enableEdgeToEdge
import com.kharchamate.resumaster.util.applySystemInsets

class TemplateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTemplateBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemplateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding.root.applySystemInsets(applyTop = true, applyBottom = true)

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}

