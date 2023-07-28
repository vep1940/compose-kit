package com.vep1940.compose.kit.graph

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.AndroidPaint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vep1940.compose.kit.util.getTextHeight
import kotlin.math.ceil
import android.graphics.Paint as NativePaint

private object Constants {
    val xAxisEndPadding = 8.dp
    val xAxisStartPadding = 24.dp

    val yAxisEndPadding = 8.dp
    val yAxisStartPadding = 8.dp

    val milestonesMarkSize = 1.dp
}


@Composable
fun Graph(
    points: List<GraphData<Int>>,
    initialXValue: Int,
    initialYValue: Int,
    xStep: Float,
    yStep: Float,
    modifier: Modifier = Modifier,
    textSize: TextUnit = 8.sp,
    pointsRadius: Float = 10f,
) {
    Box(modifier.padding(all = 2.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {

            val composePaint = AndroidPaint()
            val nativePaint = NativePaint().apply {
                this.textSize = textSize.toPx()
            }

            val startDrawingWidth = Constants.xAxisStartPadding.toPx()
            val endDrawingWidth = size.width - Constants.xAxisEndPadding.toPx()
            val startDrawingHeight = Constants.yAxisStartPadding.toPx()
            val endDrawingHeight = size.height - Constants.yAxisEndPadding.toPx()

            xAxisDrawing(
                points = points,
                initialValue = initialXValue,
                step = xStep,
                endHeight = endDrawingHeight,
                startWidth = startDrawingWidth,
                endWidth = endDrawingWidth,
                nativePaint = nativePaint,
                composePaint = composePaint,
            )

            yAxisDrawing(
                points = points,
                initialValue = initialYValue,
                step = yStep,
                startWidth = startDrawingWidth,
                startHeight = startDrawingHeight,
                endHeight = endDrawingHeight,
                nativePaint = nativePaint,
                composePaint = composePaint,
            )

        }
    }

}

private fun DrawScope.xAxisDrawing(
    points: List<GraphData<Int>>,
    initialValue: Int,
    step: Float,
    endHeight: Float,
    startWidth: Float,
    endWidth: Float,
    nativePaint: Paint,
    composePaint: AndroidPaint,
) {

    val width = endWidth - startWidth

    val xMax = points.maxOf { it.xValue }

    // xMax - initialXValue plus 1 in order to adding the initialValue to the difference
    val xAxisMilestonesCounter = (ceil((xMax - initialValue) / step) + 1).toInt()

    // xAxisMilestonesCounter - 1 in order to remove last milestone space to the right
    val spaceBetweenMilestones = width / (xAxisMilestonesCounter - 1)

    for (i in 0 until xAxisMilestonesCounter) {
        val currentWidth = startWidth + i * spaceBetweenMilestones
        val milestoneText = (initialValue + i * step).toInt().toString()
        val textWidth = nativePaint.measureText(milestoneText)

        drawContext.canvas.nativeCanvas.drawText(
            milestoneText,
            currentWidth - textWidth / 2,
            size.height,
            nativePaint
        )
        drawContext.canvas.drawLine(
            Offset(currentWidth, endHeight),
            Offset(currentWidth, endHeight + Constants.milestonesMarkSize.toPx()),
            composePaint,
        )
    }

    drawContext.canvas.drawLine(
        Offset(startWidth, endHeight),
        Offset(endWidth, endHeight),
        composePaint,
    )
}

private fun DrawScope.yAxisDrawing(
    points: List<GraphData<Int>>,
    initialValue: Int,
    step: Float,
    startWidth: Float,
    startHeight: Float,
    endHeight: Float,
    nativePaint: Paint,
    composePaint: AndroidPaint,
) {
    val height = endHeight - startHeight

    val yMax = points.maxOf { it.yValue }

    val yAxisMilestonesCounter = (ceil((yMax - initialValue) / step) + 1).toInt()

    val spaceBetweenMilestones = height / (yAxisMilestonesCounter - 1)

    for (i in 0 until yAxisMilestonesCounter) {
        val milestoneText = (initialValue + i * step).toInt().toString()

        val textHeight = nativePaint.getTextHeight(milestoneText)

        val currentHeight = endHeight - i * spaceBetweenMilestones

        drawContext.canvas.nativeCanvas.drawText(
            "${(initialValue + i * step).toInt()}",
            0f,
            currentHeight + textHeight / 2,
            nativePaint
        )
        drawContext.canvas.drawLine(
            Offset(startWidth, currentHeight),
            Offset(startWidth - Constants.milestonesMarkSize.toPx(), currentHeight),
            composePaint,
        )
    }

    drawContext.canvas.drawLine(
        Offset(Constants.xAxisStartPadding.toPx(), startHeight),
        Offset(Constants.xAxisStartPadding.toPx(), endHeight),
        composePaint,
    )
}

@Preview(showBackground = true)
@Composable
fun GraphPreview() {

    MaterialTheme {
        Graph(
            points = PreviewValues.points,
            initialXValue = 1,
            initialYValue = 10,
            xStep = 1f,
            yStep = 10f,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        )
    }
}

private object PreviewValues {
    val points by lazy {
        listOf(
            GraphData((1), 2),
            GraphData((2), 5),
            GraphData((3), 32),
            GraphData((4), 12),
            GraphData((5), 67),
            GraphData((6), 53),
            GraphData((7), 87),
            GraphData((8), 100),
            GraphData((9), 29),
            GraphData((10), 47),
            GraphData((11), 23),
            GraphData((12), 66),
            GraphData((13), 72),
            GraphData((14), 20),
            GraphData((15), 35),
            GraphData((16), 6),
            GraphData((17), 7),
            GraphData((18), 0),
            GraphData((19), 12),
            GraphData((20), 38),
            GraphData((21), 26),
            GraphData((22), 74),
            GraphData((23), 101),
            GraphData((24), 84),
            GraphData((25), 94),
            GraphData((26), 36),
            GraphData((27), 75),
            GraphData((28), 37),
            GraphData((29), 86),
            GraphData((30), 86),
            GraphData((31), 99),
        )
    }
}