package com.vep1940.compose.kit.util

import androidx.compose.ui.graphics.AndroidPaint
import androidx.compose.ui.graphics.Color

fun AndroidPaint.withColor(color: Color, content: (AndroidPaint) -> Unit) {
    val colorSaved = this.color
    this.color = color
    content(this)
    this.color = colorSaved
}