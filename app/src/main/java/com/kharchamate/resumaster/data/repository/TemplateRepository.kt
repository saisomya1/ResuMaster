package com.kharchamate.resumaster.data.repository

import com.kharchamate.resumaster.R
import com.kharchamate.resumaster.data.model.TemplateModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TemplateRepository {

    fun getTemplates(): Flow<List<TemplateModel>> = flow {
        emit(
            listOf(
                TemplateModel(
                    id = "tpl_modern_blue",
                    name = "Modern Blue",
                    category = "Modern",
                    previewImageRes = R.drawable.template_modern,
                    description = "A sleek, modern design with blue accents.",
                    isPremium = false
                ),
                TemplateModel(
                    id = "tpl_executive_pro",
                    name = "Executive Pro",
                    category = "Executive",
                    previewImageRes = R.drawable.template_professional,
                    description = "Classic layout for senior professionals.",
                    isPremium = true
                ),
                TemplateModel(
                    id = "tpl_professional_ats",
                    name = "Professional ATS",
                    category = "Standard",
                    previewImageRes = R.drawable.template_corporate,
                    description = "Optimized for Applicant Tracking Systems.",
                    isPremium = false
                ),
                TemplateModel(
                    id = "tpl_minimal_clean",
                    name = "Minimal Clean",
                    category = "Minimalist",
                    previewImageRes = R.drawable.template_minimal,
                    description = "Clean and simple with plenty of whitespace.",
                    isPremium = false
                ),
                TemplateModel(
                    id = "tpl_creative_designer",
                    name = "Creative Designer",
                    category = "Creative",
                    previewImageRes = R.drawable.template_creative,
                    description = "Showcase your creativity with this bold layout.",
                    isPremium = true
                )
            )
        )
    }
}
