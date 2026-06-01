package com.kharchamate.resumaster.data.model

data class TemplateModel(
    val id: String,
    val name: String,
    val category: String,
    val previewImageRes: Int,
    val description: String,
    val isPremium: Boolean = false
)
