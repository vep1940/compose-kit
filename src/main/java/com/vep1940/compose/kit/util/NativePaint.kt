package com.vep1940.compose.kit.util

import android.graphics.Rect
import androidx.compose.ui.graphics.NativePaint
import kotlin.math.abs

fun NativePaint.getTextHeight(text: String): Int {
    val bounds = Rect()
    getTextBounds(
        text,
        0,
        text.length - 1,
        bounds
    )
    return abs(bounds.top)
}