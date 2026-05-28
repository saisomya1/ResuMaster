package com.kharchamate.resumaster.ui.splash

sealed class SplashUiState {
    object Loading : SplashUiState()
    data class Progress(val value: Int) : SplashUiState()
    data class Completed(val shouldShowOnboarding: Boolean) : SplashUiState()
}
