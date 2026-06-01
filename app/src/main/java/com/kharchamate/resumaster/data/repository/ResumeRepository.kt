package com.kharchamate.resumaster.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.kharchamate.resumaster.data.model.ResumeData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject
import org.json.JSONArray

class ResumeRepository(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("resume_prefs", Context.MODE_PRIVATE)
    
    private val _resumeDataFlow = MutableStateFlow(loadResumeData())
    val resumeDataFlow: StateFlow<ResumeData> = _resumeDataFlow.asStateFlow()

    private fun loadResumeData(): ResumeData {
        // In a real app, this would use Gson or Moshi to deserialize from SharedPreferences.
        // For simplicity and avoiding extra dependencies, we return empty data.
        // But since we want to test empty state and let users update, we will return an empty instance.
        // Mock data could be injected here for testing if needed.
        return ResumeData()
    }

    fun updateResumeData(newData: ResumeData) {
        _resumeDataFlow.value = newData
        // Here we would serialize and save to SharedPreferences
    }
}
