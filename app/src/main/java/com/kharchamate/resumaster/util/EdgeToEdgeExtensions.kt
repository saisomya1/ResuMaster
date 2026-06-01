package com.kharchamate.resumaster.util

import android.app.Activity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding

fun Activity.enableEdgeToEdge() {
    WindowCompat.setDecorFitsSystemWindows(window, false)
}

fun View.applySystemInsets(
    applyTop: Boolean = false,
    applyBottom: Boolean = false,
    applyLeft: Boolean = false,
    applyRight: Boolean = false
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
        view.updatePadding(
            top = if (applyTop) insets.top else view.paddingTop,
            bottom = if (applyBottom) insets.bottom else view.paddingBottom,
            left = if (applyLeft) insets.left else view.paddingLeft,
            right = if (applyRight) insets.right else view.paddingRight
        )
        windowInsets
    }
}

fun View.applySystemInsetsToMargin(
    applyTop: Boolean = false,
    applyBottom: Boolean = false,
    applyLeft: Boolean = false,
    applyRight: Boolean = false
) {
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, windowInsets ->
        val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout())
        view.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            if (applyTop) topMargin = insets.top
            if (applyBottom) bottomMargin = insets.bottom
            if (applyLeft) leftMargin = insets.left
            if (applyRight) rightMargin = insets.right
        }
        windowInsets
    }
}
