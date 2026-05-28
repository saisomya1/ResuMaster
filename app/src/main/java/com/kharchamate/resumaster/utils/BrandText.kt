package com.kharchamate.resumaster.utils

import android.graphics.LinearGradient
import android.graphics.Shader
import android.text.TextPaint
import android.text.style.CharacterStyle
import android.text.style.UpdateAppearance

class GradientTextSpan(
    private val startColor: Int,
    private val endColor: Int
) : CharacterStyle(), UpdateAppearance {

    override fun updateDrawState(tp: TextPaint) {
        val textWidth = tp.measureText("Resu").coerceAtLeast(1f)
        tp.shader = LinearGradient(
            0f,
            0f,
            textWidth,
            0f,
            startColor,
            endColor,
            Shader.TileMode.CLAMP
        )
    }
}

