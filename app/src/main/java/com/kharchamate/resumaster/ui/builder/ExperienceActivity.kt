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
import com.kharchamate.resumaster.databinding.ActivityExperienceBinding
import com.kharchamate.resumaster.databinding.ItemExperienceFormBinding
import org.json.JSONArray
import org.json.JSONObject

class ExperienceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExperienceBinding
    private val PREF_NAME = "ResumeBuilderDraft"
    private val KEY_EXPERIENCE = "draft_experience"

    private val experienceForms = mutableListOf<ItemExperienceFormBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExperienceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        loadDraftData()

        if (experienceForms.isEmpty()) {
            addExperienceForm()
        }
    }

    private fun setupStatusBar() {
        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnPrevious.setOnClickListener { finish() }

        binding.btnAddExperience.setOnClickListener {
            addExperienceForm()
        }

        binding.btnSaveDraft.setOnClickListener {
            saveDraftData()
            Toast.makeText(this, "Experience Draft Saved", Toast.LENGTH_SHORT).show()
        }

        binding.btnNext.setOnClickListener {
            if (validateFields()) {
                saveDraftData()
                val intent = Intent(this, SkillsProjectsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun addExperienceForm(data: JSONObject? = null) {
        val formBinding = ItemExperienceFormBinding.inflate(LayoutInflater.from(this), binding.containerExperience, false)
        
        formBinding.btnDelete.setOnClickListener {
            binding.containerExperience.removeView(formBinding.root)
            experienceForms.remove(formBinding)
            updateTitles()
        }

        // Handle Checkbox Logic
        formBinding.cbCurrentlyWorking.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                formBinding.tilEndDate.isEnabled = false
                formBinding.etEndDate.setText("Present")
            } else {
                formBinding.tilEndDate.isEnabled = true
                if (formBinding.etEndDate.text.toString() == "Present") {
                    formBinding.etEndDate.setText("")
                }
            }
        }

        if (data != null) {
            formBinding.etCompanyName.setText(data.optString("company", ""))
            formBinding.etJobRole.setText(data.optString("role", ""))
            formBinding.etEmploymentType.setText(data.optString("type", ""))
            formBinding.etStartDate.setText(data.optString("start", ""))
            
            val isWorking = data.optBoolean("isWorking", false)
            formBinding.cbCurrentlyWorking.isChecked = isWorking
            if (!isWorking) {
                formBinding.etEndDate.setText(data.optString("end", ""))
            }
            
            formBinding.etDescription.setText(data.optString("description", ""))
        }

        binding.containerExperience.addView(formBinding.root)
        experienceForms.add(formBinding)
        updateTitles()
    }

    private fun updateTitles() {
        experienceForms.forEachIndexed { index, formBinding ->
            formBinding.tvEntryTitle.text = "Experience Entry ${index + 1}"
            if (experienceForms.size == 1) {
                formBinding.btnDelete.visibility = View.INVISIBLE
            } else {
                formBinding.btnDelete.visibility = View.VISIBLE
            }
        }
    }

    private fun saveDraftData() {
        val jsonArray = JSONArray()
        for (formBinding in experienceForms) {
            val jsonObject = JSONObject()
            jsonObject.put("company", formBinding.etCompanyName.text.toString().trim())
            jsonObject.put("role", formBinding.etJobRole.text.toString().trim())
            jsonObject.put("type", formBinding.etEmploymentType.text.toString().trim())
            jsonObject.put("start", formBinding.etStartDate.text.toString().trim())
            jsonObject.put("end", formBinding.etEndDate.text.toString().trim())
            jsonObject.put("isWorking", formBinding.cbCurrentlyWorking.isChecked)
            jsonObject.put("description", formBinding.etDescription.text.toString().trim())
            jsonArray.put(jsonObject)
        }

        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_EXPERIENCE, jsonArray.toString()).apply()
    }

    private fun loadDraftData() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_EXPERIENCE, null)
        
        if (!jsonString.isNullOrEmpty()) {
            try {
                val jsonArray = JSONArray(jsonString)
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    addExperienceForm(jsonObject)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun validateFields(): Boolean {
        if (experienceForms.isEmpty()) {
            Toast.makeText(this, "Please add at least one experience entry", Toast.LENGTH_SHORT).show()
            return false
        }

        var isValid = true

        for (formBinding in experienceForms) {
            val company = formBinding.etCompanyName.text.toString().trim()
            if (company.isEmpty()) {
                formBinding.tilCompanyName.error = "Required"
                isValid = false
            } else {
                formBinding.tilCompanyName.error = null
            }

            val role = formBinding.etJobRole.text.toString().trim()
            if (role.isEmpty()) {
                formBinding.tilJobRole.error = "Required"
                isValid = false
            } else {
                formBinding.tilJobRole.error = null
            }

            val description = formBinding.etDescription.text.toString().trim()
            if (description.isEmpty() || description.length < 10) {
                formBinding.tilDescription.error = "Description should be at least 10 characters"
                isValid = false
            } else {
                formBinding.tilDescription.error = null
            }
        }

        if (!isValid) {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_SHORT).show()
        }

        return isValid
    }
}
