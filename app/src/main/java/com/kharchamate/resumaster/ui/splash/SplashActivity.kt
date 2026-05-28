package com.kharchamate.resumaster.ui.splash

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.kharchamate.resumaster.MainActivity
import com.kharchamate.resumaster.R
import com.kharchamate.resumaster.databinding.ActivitySplashBinding
import com.kharchamate.resumaster.ui.onboarding.OnboardingActivity
import com.kharchamate.resumaster.utils.GradientTextSpan
import kotlinx.coroutines.launch
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val viewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Let OS splash hand off immediately to our premium in-app splash.
        splashScreen.setKeepOnScreenCondition { false }

        applyBrandedTitle()
        setupAnimations()
        observeState()
    }

    private fun applyBrandedTitle() {
        val full = "ResuMaster"
        val spannable = SpannableString(full)

        val start = 0
        val mid = 4
        val end = full.length

        spannable.setSpan(
            GradientTextSpan(
                startColor = getColor(R.color.primary),
                endColor = getColor(R.color.secondary)
            ),
            start,
            mid,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(
            ForegroundColorSpan(getColor(R.color.textOnPrimary)),
            mid,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.tvAppName.text = spannable
    }

    private fun setupAnimations() {
        // Logo entrance animation (scale + fade)
        binding.logoContainer.alpha = 0f
        binding.logoContainer.scaleX = 0.8f
        binding.logoContainer.scaleY = 0.8f

        val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1f)
        val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
        val alpha = PropertyValuesHolder.ofFloat(View.ALPHA, 1f)
        
        ObjectAnimator.ofPropertyValuesHolder(binding.logoContainer, scaleX, scaleY, alpha).apply {
            duration = 800
            interpolator = OvershootInterpolator()
            start()
        }

        // Soft floating effect (subtle, premium)
        ObjectAnimator.ofFloat(binding.logoContainer, View.TRANSLATION_Y, 0f, -10f, 0f).apply {
            duration = 1600
            interpolator = AccelerateDecelerateInterpolator()
            repeatCount = ObjectAnimator.INFINITE
            start()
        }

        // Progress container fade in
        binding.progressContainer.alpha = 0f
        binding.progressContainer.animate()
            .alpha(1f)
            .setStartDelay(400)
            .setDuration(500)
            .start()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is SplashUiState.Loading -> {
                            // Initial state handled by animations
                        }
                        is SplashUiState.Progress -> {
                            binding.progressBar.progress = state.value
                            binding.tvProgress.text = "${state.value}%"
                        }
                        is SplashUiState.Completed -> {
                            if (state.shouldShowOnboarding) {
                                navigateToOnboarding()
                            } else {
                                navigateToHome()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun navigateToOnboarding() {
        startActivity(Intent(this, OnboardingActivity::class.java))
        finish()
        // Smooth fade transition
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun navigateToHome() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
