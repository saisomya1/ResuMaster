package com.kharchamate.resumaster.ui.onboarding

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class DepthPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(view: View, position: Float) {
        // Premium: subtle scale + fade + vertical parallax (closer to modern SaaS onboarding).
        val clamped = position.coerceIn(-1f, 1f)
        val distance = abs(clamped)

        val scale = MIN_SCALE + (1f - MIN_SCALE) * (1f - distance)
        view.scaleX = scale
        view.scaleY = scale
        view.alpha = MIN_ALPHA + (1f - MIN_ALPHA) * (1f - distance)
        view.translationY = clamped * view.height * PARALLAX_Y
    }

    companion object {
        private const val MIN_SCALE = 0.92f
        private const val MIN_ALPHA = 0.35f
        private const val PARALLAX_Y = 0.06f
    }
}
