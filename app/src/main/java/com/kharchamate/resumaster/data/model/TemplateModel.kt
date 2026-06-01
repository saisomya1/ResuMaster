package com.kharchamate.resumaster.data.model

import androidx.annotation.LayoutRes

data class TemplateModel(
    val id: String,
    val name: String,
    val category: String,
    @LayoutRes val previewLayoutRes: Int,  // Inflatable wireframe preview layout
    val description: String,
    val isPremium: Boolean = false
)
