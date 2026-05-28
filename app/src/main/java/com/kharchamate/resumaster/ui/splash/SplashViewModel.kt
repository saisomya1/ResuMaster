package com.kharchamate.resumaster.ui.splash

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kharchamate.resumaster.utils.OnboardingPreferences
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    application: Application
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow<SplashUiState>(SplashUiState.Loading)
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    private val onboardingPreferences = OnboardingPreferences(application)

    init {
        startSplash()
    }

    private fun startSplash() {
        viewModelScope.launch {
            // Premium-feel: load up to 85%, settle, then route.
            for (i in 0..85) {
                delay(18) // ~1.5s
                _uiState.value = SplashUiState.Progress(i)
            }
            delay(500) // settle at 85% (reference)
            val shouldShowOnboarding = !onboardingPreferences.isOnboardingCompleted()
            _uiState.value = SplashUiState.Completed(shouldShowOnboarding = shouldShowOnboarding)
        }
    }
}
