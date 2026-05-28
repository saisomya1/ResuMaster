package com.kharchamate.resumaster.ui.onboarding

import androidx.lifecycle.ViewModel
import com.kharchamate.resumaster.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel : ViewModel() {

    val pages = listOf(
        OnboardingPage(
            imageRes = R.drawable.onboarding_resume,
            titleRes = R.string.onboarding_1_title,
            descriptionRes = R.string.onboarding_1_desc
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_ai_robot,
            titleRes = R.string.onboarding_2_title,
            descriptionRes = R.string.onboarding_2_desc
        ),
        OnboardingPage(
            imageRes = R.drawable.onboarding_resume_templates,
            titleRes = R.string.onboarding_3_title,
            descriptionRes = R.string.onboarding_3_desc
        )
    )

    private val _currentPage = MutableStateFlow(0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    fun setPage(pageIndex: Int) {
        _currentPage.value = pageIndex
    }
}
