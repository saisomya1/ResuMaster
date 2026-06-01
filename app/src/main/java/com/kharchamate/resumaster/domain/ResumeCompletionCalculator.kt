package com.kharchamate.resumaster.domain

import com.kharchamate.resumaster.data.model.ResumeData

object ResumeCompletionCalculator {
    fun calculate(data: ResumeData): Int {
        var progress = 0

        // Personal Information = 20%
        if (data.personalInfo?.isValid() == true) progress += 20
        
        // Professional Summary = 10%
        if (!data.summary.isNullOrBlank()) progress += 10
        
        // Education = 15%
        if (data.educations.isNotEmpty()) progress += 15
        
        // Work Experience = 20%
        if (data.experiences.isNotEmpty()) progress += 20
        
        // Skills = 15%
        if (data.skills.isNotEmpty()) progress += 15
        
        // Projects = 10%
        if (data.projects.isNotEmpty()) progress += 10
        
        // Selected Template = 10%
        if (!data.selectedTemplate.isNullOrBlank()) progress += 10

        return progress
    }
}
