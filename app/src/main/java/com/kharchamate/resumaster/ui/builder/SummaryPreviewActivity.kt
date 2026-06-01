package com.kharchamate.resumaster.ui.builder

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.kharchamate.resumaster.R
import com.kharchamate.resumaster.databinding.ActivitySummaryPreviewBinding
import org.json.JSONArray
import com.kharchamate.resumaster.util.enableEdgeToEdge
import com.kharchamate.resumaster.util.applySystemInsets

class SummaryPreviewActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySummaryPreviewBinding
    private val PREF_NAME = "ResumeBuilderDraft"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryPreviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding.root.applySystemInsets(applyTop = true, applyBottom = true)

        setupStatusBar()
        setupListeners()
        loadSummary()
        buildPreview()
    }

    private fun setupStatusBar() {
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnPrevious.setOnClickListener { finish() }

        binding.btnSaveDraft.setOnClickListener {
            saveSummary()
            Toast.makeText(this, "Summary Saved", Toast.LENGTH_SHORT).show()
            buildPreview() // Rebuild with new summary
        }

        binding.btnImproveWithAi.setOnClickListener {
            saveSummary()
            val intent = Intent(this, AiResumeActivity::class.java)
            startActivity(intent)
        }

        binding.btnGeneratePdf.setOnClickListener {
            saveSummary()
            val intent = Intent(this, ResumePdfPreviewActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadSummary() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        binding.etSummary.setText(prefs.getString("draft_summary", ""))
    }

    private fun saveSummary() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString("draft_summary", binding.etSummary.text.toString().trim()).apply()
    }

    private fun buildPreview() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val container = binding.previewContainer
        container.removeAllViews() // Clear placeholder

        // Top Info
        val name = prefs.getString("draft_name", "Your Name") ?: "Your Name"
        val email = prefs.getString("draft_email", "email@example.com") ?: "email@example.com"
        val phone = prefs.getString("draft_phone", "123-456-7890") ?: "123-456-7890"
        
        container.addView(createTextView(name, 22f, true, "#212121"))
        container.addView(createTextView("$email | $phone", 14f, false, "#757575"))
        addDivider(container)

        // Summary
        val summary = prefs.getString("draft_summary", "")
        if (!summary.isNullOrEmpty()) {
            container.addView(createTextView("SUMMARY", 16f, true, "#1976D2"))
            container.addView(createTextView(summary, 14f, false, "#424242"))
            addDivider(container)
        }

        // Experience
        val expJson = prefs.getString("draft_experience", null)
        if (!expJson.isNullOrEmpty()) {
            try {
                val array = JSONArray(expJson)
                if (array.length() > 0) {
                    container.addView(createTextView("EXPERIENCE", 16f, true, "#1976D2"))
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val title = "${obj.optString("role")} at ${obj.optString("company")}"
                        val dates = "${obj.optString("start")} - ${if (obj.optBoolean("isWorking")) "Present" else obj.optString("end")}"
                        container.addView(createTextView(title, 14f, true, "#212121"))
                        container.addView(createTextView(dates, 12f, false, "#757575"))
                        container.addView(createTextView(obj.optString("description"), 14f, false, "#424242"))
                        val spacer = View(this).apply { layoutParams = LinearLayout.LayoutParams(1, 16) }
                        container.addView(spacer)
                    }
                    addDivider(container)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }

        // Education
        val eduJson = prefs.getString("draft_education", null)
        if (!eduJson.isNullOrEmpty()) {
            try {
                val array = JSONArray(eduJson)
                if (array.length() > 0) {
                    container.addView(createTextView("EDUCATION", 16f, true, "#1976D2"))
                    for (i in 0 until array.length()) {
                        val obj = array.getJSONObject(i)
                        val title = "${obj.optString("degree")} in ${obj.optString("fieldOfStudy")}"
                        val school = "${obj.optString("school")} (${obj.optString("startYear")} - ${obj.optString("endYear")})"
                        container.addView(createTextView(title, 14f, true, "#212121"))
                        container.addView(createTextView(school, 14f, false, "#757575"))
                        val spacer = View(this).apply { layoutParams = LinearLayout.LayoutParams(1, 16) }
                        container.addView(spacer)
                    }
                    addDivider(container)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }

        // Skills
        val skillsJson = prefs.getString("draft_skills", null)
        if (!skillsJson.isNullOrEmpty()) {
            try {
                val array = JSONArray(skillsJson)
                if (array.length() > 0) {
                    container.addView(createTextView("SKILLS", 16f, true, "#1976D2"))
                    val skillsStr = buildString {
                        for (i in 0 until array.length()) {
                            append(array.getString(i))
                            if (i < array.length() - 1) append(" â€¢ ")
                        }
                    }
                    container.addView(createTextView(skillsStr, 14f, false, "#424242"))
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun createTextView(text: String, sizeSp: Float, isBold: Boolean, colorHex: String): TextView {
        return TextView(this).apply {
            this.text = text
            this.textSize = sizeSp
            this.setTextColor(Color.parseColor(colorHex))
            if (isBold) {
                this.setTypeface(null, Typeface.BOLD)
            }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }
    }

    private fun addDivider(container: LinearLayout) {
        val divider = View(this).apply {
            setBackgroundColor(Color.parseColor("#E0E0E0"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                3 // 1dp or 3px
            ).apply {
                topMargin = 16
                bottomMargin = 16
            }
        }
        container.addView(divider)
    }
}

