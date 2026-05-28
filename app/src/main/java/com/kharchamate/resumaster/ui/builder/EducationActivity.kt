package com.kharchamate.resumaster.ui.builder

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.kharchamate.resumaster.databinding.ActivityEducationBinding
import com.kharchamate.resumaster.databinding.ItemEducationFormBinding
import org.json.JSONArray
import org.json.JSONObject

class EducationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEducationBinding
    private val PREF_NAME = "ResumeBuilderDraft"
    private val KEY_EDUCATION = "draft_education"

    // Keep track of dynamically added bindings for validation and data extraction
    private val educationForms = mutableListOf<ItemEducationFormBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEducationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        loadDraftData()

        // If no data was loaded, add at least one empty form
        if (educationForms.isEmpty()) {
            addEducationForm()
        }
    }

    private fun setupStatusBar() {
        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnPrevious.setOnClickListener { finish() }

        binding.btnAddEducation.setOnClickListener {
            addEducationForm()
        }

        binding.btnSaveDraft.setOnClickListener {
            saveDraftData()
            Toast.makeText(this, "Education Draft Saved", Toast.LENGTH_SHORT).show()
        }

        binding.btnNext.setOnClickListener {
            if (validateFields()) {
                saveDraftData()
                val intent = Intent(this, ExperienceActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun addEducationForm(data: JSONObject? = null) {
        val formBinding = ItemEducationFormBinding.inflate(LayoutInflater.from(this), binding.containerEducation, false)
        
        formBinding.btnDelete.setOnClickListener {
            binding.containerEducation.removeView(formBinding.root)
            educationForms.remove(formBinding)
            updateTitles()
        }

        // Populate if data exists
        if (data != null) {
            formBinding.etSchoolName.setText(data.optString("school", ""))
            formBinding.etDegree.setText(data.optString("degree", ""))
            formBinding.etFieldOfStudy.setText(data.optString("fieldOfStudy", ""))
            formBinding.etStartYear.setText(data.optString("startYear", ""))
            formBinding.etEndYear.setText(data.optString("endYear", ""))
            formBinding.etCgpa.setText(data.optString("cgpa", ""))
            formBinding.etDescription.setText(data.optString("description", ""))
        }

        binding.containerEducation.addView(formBinding.root)
        educationForms.add(formBinding)
        updateTitles()
    }

    private fun updateTitles() {
        educationForms.forEachIndexed { index, formBinding ->
            formBinding.tvEntryTitle.text = "Education Entry ${index + 1}"
            // Hide delete button if it's the only entry
            if (educationForms.size == 1) {
                formBinding.btnDelete.visibility = View.INVISIBLE
            } else {
                formBinding.btnDelete.visibility = View.VISIBLE
            }
        }
    }

    private fun saveDraftData() {
        val jsonArray = JSONArray()
        for (formBinding in educationForms) {
            val jsonObject = JSONObject()
            jsonObject.put("school", formBinding.etSchoolName.text.toString().trim())
            jsonObject.put("degree", formBinding.etDegree.text.toString().trim())
            jsonObject.put("fieldOfStudy", formBinding.etFieldOfStudy.text.toString().trim())
            jsonObject.put("startYear", formBinding.etStartYear.text.toString().trim())
            jsonObject.put("endYear", formBinding.etEndYear.text.toString().trim())
            jsonObject.put("cgpa", formBinding.etCgpa.text.toString().trim())
            jsonObject.put("description", formBinding.etDescription.text.toString().trim())
            jsonArray.put(jsonObject)
        }

        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_EDUCATION, jsonArray.toString()).apply()
    }

    private fun loadDraftData() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_EDUCATION, null)
        
        if (!jsonString.isNullOrEmpty()) {
            try {
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    addEducationForm(jsonObject)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun validateFields(): Boolean {
        if (educationForms.isEmpty()) {
            Toast.makeText(this, "Please add at least one education entry", Toast.LENGTH_SHORT).show()
            return false
        }

        var isValid = true

        for (formBinding in educationForms) {
            val school = formBinding.etSchoolName.text.toString().trim()
            if (school.isEmpty()) {
                formBinding.tilSchoolName.error = "Required"
                isValid = false
            } else {
                formBinding.tilSchoolName.error = null
            }

            val degree = formBinding.etDegree.text.toString().trim()
            if (degree.isEmpty()) {
                formBinding.tilDegree.error = "Required"
                isValid = false
            } else {
                formBinding.tilDegree.error = null
            }

            val startYear = formBinding.etStartYear.text.toString().trim()
            if (startYear.isEmpty()) {
                formBinding.tilStartYear.error = "Required"
                isValid = false
            } else {
                formBinding.tilStartYear.error = null
            }
            
            val endYear = formBinding.etEndYear.text.toString().trim()
            if (endYear.isEmpty()) {
                formBinding.tilEndYear.error = "Required"
                isValid = false
            } else {
                formBinding.tilEndYear.error = null
            }
        }

        if (!isValid) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
        }

        return isValid
    }
}
