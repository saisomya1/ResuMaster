package com.kharchamate.resumaster.utils

import android.content.Context
import androidx.core.content.edit

class OnboardingPreferences(
    context: Context
) {
    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isOnboardingCompleted(): Boolean = prefs.getBoolean(KEY_COMPLETED, false)

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit(commit = true) {
            putBoolean(KEY_COMPLETED, completed)
        }
    }

    private companion object {
        private const val PREFS_NAME = "resumaster_prefs"
        private const val KEY_COMPLETED = "onboarding_completed"
    }
}
