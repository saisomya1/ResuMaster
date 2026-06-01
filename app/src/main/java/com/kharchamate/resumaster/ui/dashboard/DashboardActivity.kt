package com.kharchamate.resumaster.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.kharchamate.resumaster.data.repository.FirestoreRepository
import com.kharchamate.resumaster.data.repository.ResumeRepository
import com.kharchamate.resumaster.data.repository.TemplateRepository
import com.kharchamate.resumaster.databinding.ActivityDashboardBinding
import com.kharchamate.resumaster.ui.builder.ResumeBuilderActivity
import com.kharchamate.resumaster.ui.debug.FirebaseDebugActivity
import com.kharchamate.resumaster.util.applySystemInsets
import com.kharchamate.resumaster.util.enableEdgeToEdge
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDashboardBinding

    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(
            ResumeRepository(this),
            TemplateRepository(),
            FirestoreRepository()
        )
    }

    private lateinit var templateAdapter: TemplateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding.root.applySystemInsets(applyTop = true, applyBottom = true)

        setupStatusBar()
        setupClickListeners()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupStatusBar() {
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun setupClickListeners() {
        binding.cardCreateResume.setOnClickListener {
            startActivity(Intent(this, CreateResumeActivity::class.java))
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

        binding.btnViewAllTemplates.setOnClickListener {
            startActivity(Intent(this, TemplateActivity::class.java))
        }

        binding.ivProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }
        
        // Debug Easter Egg: Long press on profile icon
        binding.ivProfile.setOnLongClickListener {
            startActivity(Intent(this, FirebaseDebugActivity::class.java))
            true
        }
    }

    private fun setupRecyclerView() {
        templateAdapter = TemplateAdapter { template ->
            viewModel.selectTemplate(template.id, template.name)
        }
        binding.rvTemplates.apply {
            layoutManager = LinearLayoutManager(this@DashboardActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = templateAdapter
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe User Name
                launch {
                    viewModel.userName.collectLatest { name ->
                        binding.tvWelcomeTitle.text = "Hello, $name 👋"
                    }
                }

                // Observe Progress
                launch {
                    viewModel.resumeProgress.collectLatest { progress ->
                        binding.circularProgress.progress = progress
                        binding.tvProgressPercentage.text = "$progress%"
                    }
                }

                // Observe Templates List
                launch {
                    viewModel.templates.collectLatest { templates ->
                        templateAdapter.submitList(templates)
                    }
                }

                // Observe Selected Template (Local + Firestore)
                launch {
                    viewModel.selectedTemplateId.collectLatest { id ->
                        templateAdapter.setSelectedTemplate(id)
                    }
                }
                
                // Also observe Firestore real-time selected template updates
                launch {
                    viewModel.firestoreSelectedTemplateId.collectLatest { id ->
                        if (id != null && id != viewModel.selectedTemplateId.value) {
                             // E.g., updated from another device or View All screen
                             templateAdapter.setSelectedTemplate(id)
                        }
                    }
                }
            }
        }
    }
}
