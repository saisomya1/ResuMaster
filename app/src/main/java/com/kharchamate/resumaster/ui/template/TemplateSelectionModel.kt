package com.kharchamate.resumaster.ui.template

import androidx.annotation.LayoutRes

data class TemplateSelectionModel(
    val id: String,
    val name: String,
    val description: String,
    @LayoutRes val layoutResId: Int,
    var isSelected: Boolean = false
)
