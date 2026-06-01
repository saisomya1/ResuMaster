package com.kharchamate.resumaster.ui.builder

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.kharchamate.resumaster.databinding.ActivityResumeBuilderBinding
import com.kharchamate.resumaster.util.enableEdgeToEdge
import com.kharchamate.resumaster.util.applySystemInsets

class ResumeBuilderActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResumeBuilderBinding
    
    // SharedPreferences for saving draft data
    private val PREF_NAME = "ResumeBuilderDraft"
    private val KEY_NAME = "draft_name"
    private val KEY_EMAIL = "draft_email"
    private val KEY_PHONE = "draft_phone"
    private val KEY_LOCATION = "draft_location"
    private val KEY_LINKEDIN = "draft_linkedin"
    private val KEY_GITHUB = "draft_github"
    private val KEY_SUMMARY = "draft_summary"
    private val KEY_PROFILE_URI = "draft_profile_uri"
    
    private var selectedImageUri: String? = null

    // ActivityResultLauncher for image picking
    private val pickImageLauncher = registerForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.GetContent()
    ) { uri: android.net.Uri? ->
        if (uri != null) {
            selectedImageUri = uri.toString()
            updateProfilePhotoView(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResumeBuilderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding.root.applySystemInsets(applyTop = true, applyBottom = true)

        setupStatusBar()
        setupListeners()
        loadDraftData()
    }

    private fun setupStatusBar() {
        window.statusBarColor = Color.TRANSPARENT
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnUploadPhoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        binding.btnSaveDraft.setOnClickListener {
            saveDraftData()
            Toast.makeText(this, "Draft Saved", Toast.LENGTH_SHORT).show()
        }

        binding.btnNext.setOnClickListener {
            if (validateFields()) {
                saveDraftData() // Auto-save on next
                val intent = Intent(this, EducationActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun loadDraftData() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        binding.etFullName.setText(prefs.getString(KEY_NAME, ""))
        binding.etEmail.setText(prefs.getString(KEY_EMAIL, ""))
        binding.etPhone.setText(prefs.getString(KEY_PHONE, ""))
        binding.etLocation.setText(prefs.getString(KEY_LOCATION, ""))
        binding.etLinkedIn.setText(prefs.getString(KEY_LINKEDIN, ""))
        binding.etGitHub.setText(prefs.getString(KEY_GITHUB, ""))
        binding.etSummary.setText(prefs.getString(KEY_SUMMARY, ""))
        
        selectedImageUri = prefs.getString(KEY_PROFILE_URI, null)
        selectedImageUri?.let { uriString ->
            try {
                updateProfilePhotoView(android.net.Uri.parse(uriString))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateProfilePhotoView(uri: android.net.Uri) {
        binding.ivProfilePhoto.setImageURI(uri)
        binding.ivPlaceholderIcon.visibility = android.view.View.GONE
    }

    private fun saveDraftData() {
        val prefs = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().apply {
            putString(KEY_NAME, binding.etFullName.text.toString().trim())
            putString(KEY_EMAIL, binding.etEmail.text.toString().trim())
            putString(KEY_PHONE, binding.etPhone.text.toString().trim())
            putString(KEY_LOCATION, binding.etLocation.text.toString().trim())
            putString(KEY_LINKEDIN, binding.etLinkedIn.text.toString().trim())
            putString(KEY_GITHUB, binding.etGitHub.text.toString().trim())
            putString(KEY_SUMMARY, binding.etSummary.text.toString().trim())
            putString(KEY_PROFILE_URI, selectedImageUri)
            apply()
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true
        
        val name = binding.etFullName.text.toString().trim()
        if (name.isEmpty()) {
            binding.tilFullName.error = "Name is required"
            isValid = false
        } else {
            binding.tilFullName.error = null
        }

        val email = binding.etEmail.text.toString().trim()
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Valid email is required"
            isValid = false
        } else {
            binding.tilEmail.error = null
        }

        val phone = binding.etPhone.text.toString().trim()
        if (phone.isEmpty()) {
            binding.tilPhone.error = "Phone number is required"
            isValid = false
        } else {
            binding.tilPhone.error = null
        }
        
        val summary = binding.etSummary.text.toString().trim()
        if (summary.isNotEmpty() && summary.length < 20) {
            binding.tilSummary.error = "Summary should be at least 20 characters"
            isValid = false
        } else {
            binding.tilSummary.error = null
        }

        return isValid
    }
}

