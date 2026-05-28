package com.kharchamate.resumaster.ui.template

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.kharchamate.resumaster.R
import com.kharchamate.resumaster.databinding.ActivityTemplateSelectionBinding
import com.kharchamate.resumaster.ui.builder.ResumeBuilderActivity

class TemplateSelectionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTemplateSelectionBinding
    private var selectedTemplate: TemplateSelectionModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTemplateSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        setupRecyclerView()
    }

    private fun setupStatusBar() {
        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnContinue.setOnClickListener {
            if (selectedTemplate == null) {
                Toast.makeText(this, "Please select a template", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, ResumeBuilderActivity::class.java)
                intent.putExtra("selected_template", selectedTemplate?.name)
                startActivity(intent)
            }
        }
    }

    private fun setupRecyclerView() {
        val templates = listOf(
            TemplateSelectionModel("1", "Modern", "Clean ATS-friendly design", R.layout.layout_preview_modern),
            TemplateSelectionModel("2", "Professional", "Perfect for corporate jobs", R.layout.layout_preview_professional),
            TemplateSelectionModel("3", "Minimal", "Simple and elegant layout", R.layout.layout_preview_minimal),
            TemplateSelectionModel("4", "Corporate", "Traditional structure for execs", R.layout.layout_preview_corporate),
            TemplateSelectionModel("5", "Creative", "Stand out from the crowd", R.layout.layout_preview_creative)
        )

        val adapter = TemplateSelectionAdapter(templates) { selected ->
            selectedTemplate = selected
        }

        // Use a grid with 2 columns
        binding.rvTemplates.layoutManager = GridLayoutManager(this, 2)
        binding.rvTemplates.adapter = adapter
    }
}
