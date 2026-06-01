package com.kharchamate.resumaster.ui.builder

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.kharchamate.resumaster.databinding.ActivityResumePdfPreviewBinding
import com.kharchamate.resumaster.ui.dashboard.DashboardActivity
import com.kharchamate.resumaster.util.enableEdgeToEdge
import com.kharchamate.resumaster.util.applySystemInsets

class ResumePdfPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResumePdfPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResumePdfPreviewBinding.inflate(layoutInflater)
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

        binding.btnBackToDashboard.setOnClickListener {
            // Clear the resume builder back stack and go back to dashboard
            val intent = Intent(this, DashboardActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}

