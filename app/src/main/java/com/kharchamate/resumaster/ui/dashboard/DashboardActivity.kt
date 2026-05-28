package com.kharchamate.resumaster.ui.dashboard

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.kharchamate.resumaster.R
import com.kharchamate.resumaster.databinding.ActivityDashboardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupQuickActions()
        setupTemplates()
        setupRecentResume()
        setupBottomNavigation()
    }

    private fun setupToolbar() {
        binding.btnMenu.setOnClickListener {
            showToast("Hamburger Menu Clicked")
        }
        binding.btnNotification.setOnClickListener {
            showToast("Notifications Checked")
        }
        binding.ivProfile.setOnClickListener {
            showToast("Profile Settings Opened")
        }
    }

    private fun setupQuickActions() {
        binding.btnContinueResume.setOnClickListener {
            showToast("Continuing your Android Developer Resume...")
        }
        binding.btnViewAllActions.setOnClickListener {
            showToast("Viewing all Quick Actions")
        }

        binding.cardCreateResume.setOnClickListener {
            showToast("Creating a new professional resume...")
        }
        binding.cardMyResumes.setOnClickListener {
            showToast("Opening My Resumes portfolio...")
        }
        binding.cardAiImprove.setOnClickListener {
            showToast("Launching AI Resume Optimizer...")
        }
        binding.cardDownloadPdf.setOnClickListener {
            showToast("Downloading your active resume as PDF...")
        }
    }

    private fun setupTemplates() {
        binding.btnViewAllTemplates.setOnClickListener {
            showToast("Opening Resume Templates gallery")
        }

        val templates = listOf("Modern", "Professional", "Minimal", "Corporate", "Creative")
        val adapter = TemplateAdapter(templates) { templateName ->
            showToast("Selected $templateName Template")
        }

        binding.rvTemplates.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTemplates.adapter = adapter
    }

    private fun setupRecentResume() {
        binding.cardRecentResume.setOnClickListener {
            showToast("Opening 'Android Developer Resume' editor...")
        }
        binding.btnRecentOptions.setOnClickListener {
            showToast("Options for 'Android Developer Resume'")
        }
    }

    private fun setupBottomNavigation() {
        val tabs = listOf(
            Triple(binding.tabHome, binding.ivTabHome, binding.tvTabHome),
            Triple(binding.tabResume, binding.ivTabResume, binding.tvTabResume),
            Triple(binding.tabTemplates, binding.ivTabTemplates, binding.tvTabTemplates),
            Triple(binding.tabProfile, binding.ivTabProfile, binding.tvTabProfile)
        )

        tabs.forEachIndexed { index, (layout, icon, text) ->
            layout.setOnClickListener {
                // Update navigation visual state
                tabs.forEach { (_, ic, tx) ->
                    ic.setColorFilter(ContextCompat.getColor(this, R.color.textSecondary))
                    tx.textColor = ContextCompat.getColor(this, R.color.textSecondary)
                    tx.setTypeface(null, android.graphics.Typeface.NORMAL)
                }

                icon.setColorFilter(ContextCompat.getColor(this, R.color.secondary))
                text.textColor = ContextCompat.getColor(this, R.color.secondary)
                text.setTypeface(null, android.graphics.Typeface.BOLD)

                val tabName = text.text.toString()
                showToast("Navigated to $tabName tab")
            }
        }
    }

    // Helper property to modify textColor directly
    private var TextView.textColor: Int
        get() = currentTextColor
        set(value) = setTextColor(value)

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
