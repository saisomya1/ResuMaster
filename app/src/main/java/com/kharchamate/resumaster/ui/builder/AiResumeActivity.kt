package com.kharchamate.resumaster.ui.builder

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.kharchamate.resumaster.databinding.ActivityAiResumeBinding
import com.kharchamate.resumaster.util.enableEdgeToEdge
import com.kharchamate.resumaster.util.applySystemInsets

class AiResumeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAiResumeBinding
    private var aiGenerated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiResumeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding.root.applySystemInsets(applyTop = true, applyBottom = true)

        setupStatusBar()
        setupListeners()
    }

    private fun setupStatusBar() {
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }

        binding.btnGenerateAi.setOnClickListener {
            val anyChecked = binding.cbSummary.isChecked ||
                    binding.cbExperience.isChecked ||
                    binding.cbAts.isChecked

            if (!anyChecked) {
                Toast.makeText(this, "Please select at least one enhancement option", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Simulate loading
            binding.btnGenerateAi.isEnabled = false
            binding.btnGenerateAi.text = "Generating..."

            // Simulate AI delay with Handler
            binding.root.postDelayed({
                aiGenerated = true
                binding.btnGenerateAi.isEnabled = true
                binding.btnGenerateAi.text = "âœ¨ Regenerate AI Resume âœ¨"
                binding.llMockResult.visibility = View.VISIBLE
                Toast.makeText(this, "AI enhancement ready! (Mock)", Toast.LENGTH_SHORT).show()
            }, 1500)
        }

        binding.btnGeneratePdf.setOnClickListener {
            val intent = Intent(this, ResumePdfPreviewActivity::class.java)
            startActivity(intent)
        }
    }
}

