package com.kharchamate.resumaster.ui.dashboard

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.kharchamate.resumaster.databinding.ActivityTemplateBinding
import com.kharchamate.resumaster.data.repository.FirestoreRepository
import com.kharchamate.resumaster.data.repository.TemplateRepository
import com.kharchamate.resumaster.ui.template.TemplateSelectionActivity
import com.kharchamate.resumaster.ui.template.TemplateSelectionAdapter
import com.kharchamate.resumaster.ui.template.TemplateSelectionModel
import com.kharchamate.resumaster.util.applySystemInsets
import com.kharchamate.resumaster.util.enableEdgeToEdge
import com.kharchamate.resumaster.R

class TemplateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTemplateBinding
    private val firestoreRepository = FirestoreRepository()
    private var selectedTemplate: TemplateSelectionModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemplateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding.root.applySystemInsets(applyTop = true, applyBottom = true)

        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        binding.btnBack.setOnClickListener { finish() }
        binding.btnUseTemplate.setOnClickListener { onUseTemplate() }

        setupTemplateGrid()
    }

    private fun setupTemplateGrid() {
        val templates = listOf(
            TemplateSelectionModel("tpl_modern_blue",    "Modern Blue",      "Clean ATS-friendly design",       R.layout.layout_preview_modern),
            TemplateSelectionModel("tpl_executive_pro",  "Executive Pro",    "Perfect for senior professionals", R.layout.layout_preview_professional),
            TemplateSelectionModel("tpl_professional_ats","Professional ATS", "Optimized for ATS systems",       R.layout.layout_preview_corporate),
            TemplateSelectionModel("tpl_minimal_clean",  "Minimal Clean",    "Simple and elegant layout",        R.layout.layout_preview_minimal),
            TemplateSelectionModel("tpl_creative_designer","Creative Designer","Stand out from the crowd",       R.layout.layout_preview_creative)
        )

        val adapter = TemplateSelectionAdapter(templates) { selected ->
            selectedTemplate = selected
        }

        binding.rvTemplates.layoutManager = GridLayoutManager(this, 2)
        binding.rvTemplates.adapter = adapter
    }

    private fun onUseTemplate() {
        val template = selectedTemplate
        if (template == null) {
            Toast.makeText(this, "Please select a template first", Toast.LENGTH_SHORT).show()
            return
        }

        // Persist to Firestore
        firestoreRepository.saveSelectedTemplate(template.id, template.name) { success, error ->
            runOnUiThread {
                if (success) {
                    Toast.makeText(this, "\"${template.name}\" selected!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, com.kharchamate.resumaster.ui.builder.ResumeBuilderActivity::class.java)
                    intent.putExtra("selected_template", template.name)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Saved locally. Sync pending: $error", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, com.kharchamate.resumaster.ui.builder.ResumeBuilderActivity::class.java)
                    intent.putExtra("selected_template", template.name)
                    startActivity(intent)
                }
            }
        }
    }
}
