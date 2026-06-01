package com.kharchamate.resumaster.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kharchamate.resumaster.data.repository.ResumeRepository
import com.kharchamate.resumaster.domain.ResumeCompletionCalculator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DashboardViewModel(private val repository: ResumeRepository) : ViewModel() {

    val resumeProgress: StateFlow<Int> = repository.resumeDataFlow
        .map { data ->
            ResumeCompletionCalculator.calculate(data)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )
}

class DashboardViewModelFactory(private val repository: ResumeRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
