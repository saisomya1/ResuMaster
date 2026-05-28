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
import com.google.android.material.chip.Chip
import com.kharchamate.resumaster.databinding.ActivitySkillsProjectsBinding
import com.kharchamate.resumaster.databinding.ItemProjectFormBinding
import org.json.JSONArray
import org.json.JSONObject

class SkillsProjectsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySkillsProjectsBinding
    private val PREF_NAME = "ResumeBuilderDraft"
    private val KEY_SKILLS = "draft_skills"
    private val KEY_PROJECTS = "draft_projects"

    private val skillsList = mutableListOf<String>()
    private val projectForms = mutableListOf<ItemProjectFormBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySkillsProjectsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupStatusBar()
        setupListeners()
        loadDraftData()
    }

    private fun setupStatusBar() {
        window.statusBarColor = Color.WHITE
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnPrevious.setOnClickListener { finish() }

        // Skills Logic
        binding.btnAddSkill.setOnClickListener {
            val skill = binding.etSkill.text.toString().trim()
            if (skill.isNotEmpty()) {
                addSkillChip(skill)
                binding.etSkill.setText("")
            } else {
                Toast.makeText(this, "Enter a skill", Toast.LENGTH_SHORT).show()
            }
        }

        // Projects Logic
        binding.btnAddProject.setOnClickListener {
            addProjectForm()
        }

        binding.btnSaveDraft.setOnClickListener {
            saveDraftData()
            Toast.makeText(this, "Skills & Projects Draft Saved", Toast.LENGTH_SHORT).show()
        }

        binding.btnNext.setOnClickListener {
            if (validateFields()) {
                saveDraftData()
                val intent = Intent(this, SummaryPreviewActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun addSkillChip(skill: String) {
        if (!skillsList.contains(skill)) {
            skillsList.add(skill)
            
            val chip = Chip(this)
            chip.text = skill
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                binding.cgSkills.removeView(chip)
                skillsList.remove(skill)
            }
            binding.cgSkills.addView(chip)
        } else {
            Toast.makeText(this, "Skill already added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addProjectForm(data: JSONObject? = null) {
        val formBinding = ItemProjectFormBinding.inflate(LayoutInflater.from(this), binding.containerProjects, false)
        
        formBinding.btnDelete.setOnClickListener {
            binding.containerProjects.removeView(formBinding.root)
            projectForms.remove(formBinding)
            updateTitles()
        }

        if (data != null) {
            formBinding.etProjectName.setText(data.optString("name", ""))
            formBinding.etTechStack.setText(data.optString("techStack", ""))
            formBinding.etGitHubLink.setText(data.optString("link", ""))
            formBinding.etDescription.setText(data.optString("description", ""))
        }

        binding.containerProjects.addView(formBinding.root)
        projectForms.add(formBinding)
        updateTitles()
    }

    private fun updateTitles() {
        projectForms.forEachIndexed { index, formBinding ->
            formBinding.tvEntryTitle.text = "Project Entry ${index + 1}"
        }
    }

    private fun saveDraftData() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()

        // Save Skills
        val skillsArray = JSONArray()
        for (skill in skillsList) {
            skillsArray.put(skill)
        }
        editor.putString(KEY_SKILLS, skillsArray.toString())

        // Save Projects
        val projectsArray = JSONArray()
        for (formBinding in projectForms) {
            val jsonObject = JSONObject()
            jsonObject.put("name", formBinding.etProjectName.text.toString().trim())
            jsonObject.put("techStack", formBinding.etTechStack.text.toString().trim())
            jsonObject.put("link", formBinding.etGitHubLink.text.toString().trim())
            jsonObject.put("description", formBinding.etDescription.text.toString().trim())
            projectsArray.put(jsonObject)
        }
        editor.putString(KEY_PROJECTS, projectsArray.toString())
        
        editor.apply()
    }

    private fun loadDraftData() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        
        // Load Skills
        val skillsJson = prefs.getString(KEY_SKILLS, null)
        if (!skillsJson.isNullOrEmpty()) {
            try {
                val jsonArray = JSONArray(skillsJson)
                for (i in 0 until jsonArray.length()) {
                    addSkillChip(jsonArray.getString(i))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Load Projects
        val projectsJson = prefs.getString(KEY_PROJECTS, null)
        if (!projectsJson.isNullOrEmpty()) {
            try {
                val jsonArray = JSONArray(projectsJson)
                for (i in 0 until jsonArray.length()) {
                    addProjectForm(jsonArray.getJSONObject(i))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun validateFields(): Boolean {
        // We will make projects optional, but if added, they must be valid.
        var isValid = true

        for (formBinding in projectForms) {
            val name = formBinding.etProjectName.text.toString().trim()
            if (name.isEmpty()) {
                formBinding.tilProjectName.error = "Required"
                isValid = false
            } else {
                formBinding.tilProjectName.error = null
            }
            
            val tech = formBinding.etTechStack.text.toString().trim()
            if (tech.isEmpty()) {
                formBinding.tilTechStack.error = "Required"
                isValid = false
            } else {
                formBinding.tilTechStack.error = null
            }
            
            val desc = formBinding.etDescription.text.toString().trim()
            if (desc.isEmpty()) {
                formBinding.tilDescription.error = "Required"
                isValid = false
            } else {
                formBinding.tilDescription.error = null
            }
        }

        if (!isValid) {
            Toast.makeText(this, "Please fill in all required project fields", Toast.LENGTH_SHORT).show()
        }
        
        // Skills might be required. Let's enforce at least 1 skill.
        if (skillsList.isEmpty()) {
            Toast.makeText(this, "Please add at least one skill", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }
}
