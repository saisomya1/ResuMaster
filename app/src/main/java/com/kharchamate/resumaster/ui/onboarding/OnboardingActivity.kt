package com.kharchamate.resumaster.ui.onboarding

import android.content.Intent
import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.kharchamate.resumaster.R
import com.kharchamate.resumaster.MainActivity
import com.kharchamate.resumaster.databinding.ActivityOnboardingBinding
import com.kharchamate.resumaster.utils.OnboardingPreferences

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val viewModel: OnboardingViewModel by viewModels()
    private lateinit var onboardingPreferences: OnboardingPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onboardingPreferences = OnboardingPreferences(this)

        applyEdgeToEdgeInsets()
        setupViewPager()
        setupIndicators()
        setupListeners()
    }

    private fun applyEdgeToEdgeInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            binding.topBar.setPadding(
                binding.topBar.paddingLeft,
                systemBars.top,
                binding.topBar.paddingRight,
                binding.topBar.paddingBottom
            )
            binding.bottomContainer.setPadding(
                binding.bottomContainer.paddingLeft,
                binding.bottomContainer.paddingTop,
                binding.bottomContainer.paddingRight,
                systemBars.bottom + resources.getDimensionPixelSize(R.dimen.spacing_24)
            )
            insets
        }
    }

    private fun setupViewPager() {
        val adapter = OnboardingAdapter(this, viewModel.pages)
        binding.viewPager.adapter = adapter
        binding.viewPager.setPageTransformer(DepthPageTransformer())
        binding.viewPager.offscreenPageLimit = 1
        binding.viewPager.setPadding(0, 0, 0, 0)
        binding.viewPager.clipToPadding = false
        binding.viewPager.clipChildren = false

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                viewModel.setPage(position)
                updateIndicators(position)
                updateButtons(position)
            }
        })
    }

    private fun setupIndicators() {
        binding.indicatorContainer.removeAllViews()

        val dotSize = resources.getDimensionPixelSize(R.dimen.onboarding_indicator_dot)
        val dotGap = resources.getDimensionPixelSize(R.dimen.onboarding_indicator_gap)

        repeat(viewModel.pages.size) { index ->
            val dot = View(this).apply {
                layoutParams = ViewGroup.MarginLayoutParams(dotSize, dotSize).apply {
                    marginEnd = if (index == viewModel.pages.lastIndex) 0 else dotGap
                }
                background = getDrawable(R.drawable.indicator_dot_inactive_modern)
            }
            binding.indicatorContainer.addView(dot)
        }

        updateIndicators(0)
    }

    private fun updateIndicators(position: Int) {
        val activeW = resources.getDimensionPixelSize(R.dimen.onboarding_indicator_active_width)
        val dotSize = resources.getDimensionPixelSize(R.dimen.onboarding_indicator_dot)

        for (i in 0 until binding.indicatorContainer.childCount) {
            val dot = binding.indicatorContainer.getChildAt(i)
            val lp = dot.layoutParams as ViewGroup.MarginLayoutParams
            val isActive = i == position

            lp.width = if (isActive) activeW else dotSize
            lp.height = dotSize
            dot.layoutParams = lp
            dot.background = getDrawable(
                if (isActive) R.drawable.indicator_dot_active_modern else R.drawable.indicator_dot_inactive_modern
            )
        }
    }

    private fun updateButtons(position: Int) {
        val isLastPage = position == viewModel.pages.size - 1
        binding.btnSkip.visibility = if (isLastPage) View.INVISIBLE else View.VISIBLE
        binding.btnNext.setImageResource(if (isLastPage) R.drawable.ic_check_24 else R.drawable.ic_arrow_right_24)
    }

    private fun setupListeners() {
        binding.btnNext.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            val current = binding.viewPager.currentItem
            if (current + 1 < viewModel.pages.size) {
                binding.viewPager.currentItem = current + 1
            } else {
                navigateToHome()
            }
        }

        binding.btnSkip.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        onboardingPreferences.setOnboardingCompleted(true)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }
}
