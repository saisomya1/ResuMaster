package com.kharchamate.resumaster.ui.debug

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsControllerCompat
import com.kharchamate.resumaster.data.repository.FirestoreRepository
import com.kharchamate.resumaster.databinding.ActivityFirebaseDebugBinding
import com.kharchamate.resumaster.util.applySystemInsets
import com.kharchamate.resumaster.util.enableEdgeToEdge
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FirebaseDebugActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFirebaseDebugBinding
    private val firestoreRepository = FirestoreRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFirebaseDebugBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        binding.root.applySystemInsets(applyTop = true, applyBottom = true)

        window.statusBarColor = Color.parseColor("#1E1E1E")
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = false

        binding.btnBack.setOnClickListener { finish() }

        checkAuthStatus()
        setupListeners()
    }

    private fun checkAuthStatus() {
        val uid = firestoreRepository.currentUid
        if (uid != null) {
            binding.tvAuthUid.text = "UID: $uid"
            binding.tvAuthUid.setTextColor(Color.parseColor("#4CAF50")) // Green
            log("Auth: Logged in successfully.")
        } else {
            binding.tvAuthUid.text = "UID: null (Not Authenticated)"
            binding.tvAuthUid.setTextColor(Color.parseColor("#F44336")) // Red
            log("Auth: User is NOT logged in. Firestore writes will fail.")
        }
    }

    private fun setupListeners() {
        binding.btnTestRead.setOnClickListener {
            log("Executing Test Read...")
            firestoreRepository.testRead { success, message ->
                log(message)
            }
        }

        binding.btnTestWrite.setOnClickListener {
            log("Executing Test Write...")
            firestoreRepository.testWrite { success, message ->
                log(message)
            }
        }
    }

    private fun log(message: String) {
        val time = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date())
        val currentText = binding.tvLogOutput.text.toString()
        val newEntry = "[$time] $message\n"
        binding.tvLogOutput.text = newEntry + currentText
    }
}
