package com.vep1940.compose.kit.util

import android.graphics.Color
import android.graphics.Rect
import androidx.annotation.ColorInt
import androidx.compose.ui.graphics.NativePaint
import androidx.compose.ui.graphics.toArgb
import androidx.core.graphics.alpha
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import kotlin.math.abs
import androidx.compose.ui.graphics.Color as ComposeColor

fun NativePaint.drawInColor(color: ComposeColor, content: (NativePaint) -> Unit) {
    with(color.toArgb()) {
        this@drawInColor.drawInColor(
            color = Color.argb(this.alpha, this.red, this.green, this.blue),
            content = content
        )
    }
}

fun NativePaint.drawInColor(@ColorInt color: Int, content: (NativePaint) -> Unit) {
    val colorSaved = this.color
    this.color = color
    content(this)
    this.color = colorSaved
}

fun NativePaint.drawInTextSize(textSize: Float, content: (NativePaint) -> Unit) {
    val textSizeSaved = this.textSize
    this.textSize = textSize
    content(this)
    this.textSize = textSizeSaved
}


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