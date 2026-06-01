package com.kharchamate.resumaster.data.model

data class ResumeData(
    val personalInfo: PersonalInfo? = null,
    val summary: String? = null,
    val educations: List<Education> = emptyList(),
    val experiences: List<Experience> = emptyList(),
    val skills: List<String> = emptyList(),
    val projects: List<Project> = emptyList(),
    val selectedTemplate: String? = null
)

data class PersonalInfo(
    val fullName: String,
    val email: String,
    val phone: String
) {
    fun isValid(): Boolean {
        return fullName.isNotBlank() && email.isNotBlank() && phone.isNotBlank()
    }
}

data class Education(
    val id: String,
    val school: String,
    val degree: String
)

data class Experience(
    val id: String,
    val company: String,
    val role: String
)

data class Project(
    val id: String,
    val name: String,
    val description: String
)
