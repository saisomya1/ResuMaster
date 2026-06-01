package com.kharchamate.resumaster.ui.dashboard

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
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
import com.kharchamate.resumaster.data.repository.TemplateRepository
import com.kharchamate.resumaster.databinding.ActivityDashboardBinding
import com.kharchamate.resumaster.util.applySystemInsets
import com.kharchamate.resumaster.util.enableEdgeToEdge
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var templateAdapter: TemplateAdapter

    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(
            resumeRepository = ResumeRepository(this.applicationContext),
            templateRepository = TemplateRepository()
        )
    }

    // Permission launcher for Android 13+ notifications
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission granted
            } else {
                // Permission denied - handle gracefully
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
                // Observe resume completion progress
                launch {
                    viewModel.resumeProgress.collect { progress ->
                        binding.circularProgress.progress = progress
                        binding.progressBarHorizontal.progress = progress
                        binding.tvProgressPercentage.text = "${progress}%"

                        binding.tvProgressStatus.text = when {
                            progress == 0 -> "Start building your resume to increase completion percentage."
                            progress < 100 -> "You're almost there! Complete more sections to make it perfect."
                            else -> "Your resume is 100% complete! Ready to land your dream job."
                        }
                    }
                }

                // Observe template list
                launch {
                    viewModel.templates.collect { templates ->
                        templateAdapter.submitList(templates)
                    }
                }

                // Observe selected template — update adapter highlight
                launch {
                    viewModel.selectedTemplateId.collect { selectedId ->
                        templateAdapter.setSelectedTemplate(selectedId)
                    }
                }
            }
        }
    }

    private fun setupStatusBar() {
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun checkFirstTimePermissions() {
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

        templateAdapter = TemplateAdapter { template ->
            viewModel.selectTemplate(template.id)
            showToast("Template Selected: ${template.name}")
        }

        binding.rvTemplates.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvTemplates.adapter = templateAdapter
        // Disable nested scroll interference
        binding.rvTemplates.isNestedScrollingEnabled = false
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
                tabs.forEach { (_, ic, tx) ->
                    ic.setColorFilter(ContextCompat.getColor(this, R.color.textSecondary))
                    tx.textColor = ContextCompat.getColor(this, R.color.textSecondary)
                    tx.setTypeface(null, android.graphics.Typeface.NORMAL)
                }

                icon.setColorFilter(ContextCompat.getColor(this, R.color.secondary))
                text.textColor = ContextCompat.getColor(this, R.color.secondary)
                text.setTypeface(null, android.graphics.Typeface.BOLD)

                when (index) {
                    0 -> { /* Stay on Dashboard */ }
                    1 -> startActivity(Intent(this, MyResumeActivity::class.java))
                    2 -> startActivity(Intent(this, TemplateActivity::class.java))
                    3 -> startActivity(Intent(this, ProfileActivity::class.java))
                }
            }
        }
    }

    // Helper extension to set textColor directly on TextView
    private var TextView.textColor: Int
        get() = currentTextColor
        set(value) { setTextColor(value) }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
