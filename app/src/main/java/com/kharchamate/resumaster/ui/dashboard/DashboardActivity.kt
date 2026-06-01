package com.kharchamate.resumaster.ui.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kharchamate.resumaster.R
import com.kharchamate.resumaster.data.repository.ResumeRepository
import com.kharchamate.resumaster.databinding.ActivityDashboardBinding
import kotlinx.coroutines.launch
import com.kharchamate.resumaster.util.enableEdgeToEdge
import com.kharchamate.resumaster.util.applySystemInsets


class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    
    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(ResumeRepository(this.applicationContext))
    }

    // Permission launcher for Android 13+ notifications
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted
            } else {
                // Permission denied - handle gracefully, show toast or ignore since it's just a dashboard
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        enableEdgeToEdge()
        binding.root.applySystemInsets(applyTop = true, applyBottom = true)

        setupStatusBar()
        checkFirstTimePermissions()

        setupToolbar()
        setupQuickActions()
        setupTemplates()
        setupRecentResume()
        setupBottomNavigation()
        
        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resumeProgress.collect { progress ->
                    binding.circularProgress.progress = progress
                    binding.progressBarHorizontal.progress = progress
                    binding.tvProgressPercentage.text = "${progress}%"
                    
                    if (progress == 0) {
                        binding.tvProgressStatus.text = "Start building your resume to increase completion percentage."
                    } else if (progress < 100) {
                        binding.tvProgressStatus.text = "You're almost there! Complete more sections to make it perfect."
                    } else {
                        binding.tvProgressStatus.text = "Your resume is 100% complete! Ready to land your dream job."
                    }
                }
            }
        }
    }

    private fun setupStatusBar() {
        // Change status bar to transparent for edge-to-edge
        window.statusBarColor = Color.TRANSPARENT
        
        // Ensure status bar icons and text are dark
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun checkFirstTimePermissions() {
        // Request Notification permission for Android 13+ (API 33+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupToolbar() {
        binding.btnMenu.setOnClickListener {
            showToast("Hamburger Menu Clicked")
        }
        binding.btnNotification.setOnClickListener {
            showToast("Notifications Checked")
        }
        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
    }

    private fun setupQuickActions() {
        binding.btnContinueResume.setOnClickListener {
            // Dummy check for incomplete resume
            val hasIncompleteResume = false
            
            if (hasIncompleteResume) {
                startActivity(Intent(this, com.kharchamate.resumaster.ui.builder.ResumeBuilderActivity::class.java))
            } else {
                startActivity(Intent(this, com.kharchamate.resumaster.ui.template.TemplateSelectionActivity::class.java))
            }
        }
        binding.btnViewAllActions.setOnClickListener {
            showToast("Viewing all Quick Actions")
        }

        binding.cardCreateResume.setOnClickListener {
            startActivity(Intent(this, com.kharchamate.resumaster.ui.template.TemplateSelectionActivity::class.java))
        }
        binding.cardMyResumes.setOnClickListener {
            startActivity(Intent(this, MyResumeActivity::class.java))
        }
        binding.cardAiImprove.setOnClickListener {
            startActivity(Intent(this, AiImproveActivity::class.java))
        }
        binding.cardDownloadPdf.setOnClickListener {
            startActivity(Intent(this, ExportResumeActivity::class.java))
        }
    }

    private fun setupTemplates() {
        binding.btnViewAllTemplates.setOnClickListener {
            startActivity(Intent(this, TemplateActivity::class.java))
        }

        val templates = listOf("Modern", "Professional", "Minimal", "Corporate", "Creative")
        val adapter = TemplateAdapter(templates) { templateName ->
            showToast("Template Selected: $templateName")
        }

        binding.rvTemplates.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTemplates.adapter = adapter
    }

    private fun setupRecentResume() {
        binding.cardRecentResume.setOnClickListener {
            startActivity(Intent(this, MyResumeActivity::class.java))
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

                // Handle navigation logic
                when (index) {
                    0 -> {
                        // Stay on Dashboard (Home)
                    }
                    1 -> {
                        startActivity(Intent(this, MyResumeActivity::class.java))
                    }
                    2 -> {
                        startActivity(Intent(this, TemplateActivity::class.java))
                    }
                    3 -> {
                        startActivity(Intent(this, ProfileActivity::class.java))
                    }
                }
            }
        }
    }

    // Helper property to modify textColor directly
    private var TextView.textColor: Int
        get() = currentTextColor
        set(value) { setTextColor(value) }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
