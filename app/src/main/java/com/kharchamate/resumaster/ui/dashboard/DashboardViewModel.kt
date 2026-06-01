package com.kharchamate.resumaster.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.kharchamate.resumaster.data.model.TemplateModel
import com.kharchamate.resumaster.data.repository.ResumeRepository
import com.kharchamate.resumaster.data.repository.TemplateRepository
import com.kharchamate.resumaster.domain.ResumeCompletionCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val resumeRepository: ResumeRepository,
    private val templateRepository: TemplateRepository
) : ViewModel() {

    // --- Resume Completion Progress ---
    val resumeProgress: StateFlow<Int> = resumeRepository.resumeDataFlow
        .map { data -> ResumeCompletionCalculator.calculate(data) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // --- Templates List ---
    val templates: StateFlow<List<TemplateModel>> = templateRepository.getTemplates()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Selected Template ID ---
    private val _selectedTemplateId = MutableStateFlow<String?>(
        resumeRepository.resumeDataFlow.value.selectedTemplate
    )
    val selectedTemplateId: StateFlow<String?> = _selectedTemplateId.asStateFlow()

    fun selectTemplate(templateId: String) {
        _selectedTemplateId.value = templateId
        viewModelScope.launch {
            val currentData = resumeRepository.resumeDataFlow.value
            resumeRepository.updateResumeData(currentData.copy(selectedTemplate = templateId))
        }
    }
}

class DashboardViewModelFactory(
    private val resumeRepository: ResumeRepository,
    private val templateRepository: TemplateRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(resumeRepository, templateRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
