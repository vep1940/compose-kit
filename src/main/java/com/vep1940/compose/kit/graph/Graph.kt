package com.vep1940.compose.kit.graph

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.AndroidPaint
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vep1940.compose.kit.util.drawInColor
import com.vep1940.compose.kit.util.drawInTextSize
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
    xTextSize: TextUnit = 8.sp,
    xTextColor: Color = Color.Black,
    xMilestonesColor: Color = Color.Black,
    xAxisColor: Color = Color.Black,
    yTextSize: TextUnit = 8.sp,
    yTextColor: Color = Color.Black,
    yMilestonesColor: Color = Color.Black,
    yAxisColor: Color = Color.Black,
    pointsRadius: Dp = 4.dp,
    pointsColor: Color = Color.Black,
    lineWidth: Dp = 1.dp,
    lineColor: Color = Color.Black,
    areaColor: List<Color> = listOf(Color.Transparent, Color.Transparent)
) {
    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {

        val composePaint = AndroidPaint()
        val nativePaint = NativePaint()

        val xMax = points.maxOf { it.xValue }
        val yMax = points.maxOf { it.yValue }

        // maxValue - initialValue plus 1 in order to adding the initialValue to the difference
        val xAxisMilestonesCounter = (ceil((xMax - initialXValue) / xStep) + 1).toInt()
        val yAxisMilestonesCounter = (ceil((yMax - initialYValue) / yStep) + 1).toInt()

        val startDrawingWidth = Constants.xAxisStartPadding.toPx()
        val endDrawingWidth = size.width - Constants.xAxisEndPadding.toPx()
        val startDrawingHeight = Constants.yAxisStartPadding.toPx()
        val endDrawingHeight = size.height - Constants.yAxisEndPadding.toPx()

        xAxisDrawing(
            milestonesCounter = xAxisMilestonesCounter,
            initialValue = initialXValue,
            step = xStep,
            endHeight = endDrawingHeight,
            startWidth = startDrawingWidth,
            endWidth = endDrawingWidth,
            textSize = xTextSize,
            textColor = xTextColor,
            milestonesColor = xMilestonesColor,
            axisColor = xAxisColor,
            nativePaint = nativePaint,
            composePaint = composePaint,
        )

        yAxisDrawing(
            yAxisMilestonesCounter = yAxisMilestonesCounter,
            initialValue = initialYValue,
            step = yStep,
            startWidth = startDrawingWidth,
            startHeight = startDrawingHeight,
            endHeight = endDrawingHeight,
            textSize = yTextSize,
            textColor = yTextColor,
            milestonesColor = yMilestonesColor,
            axisColor = yAxisColor,
            nativePaint = nativePaint,
            composePaint = composePaint,
        )

        dataDrawing(
            xAxisMilestonesCounter = xAxisMilestonesCounter,
            xStep = xStep,
            initialXValue = initialXValue,
            yAxisMilestonesCounter = yAxisMilestonesCounter,
            yStep = yStep,
            initialYValue = initialYValue,
            endDrawingWidth = endDrawingWidth,
            startDrawingWidth = startDrawingWidth,
            endDrawingHeight = endDrawingHeight,
            startDrawingHeight = startDrawingHeight,
            points = points,
            pointsRadius = pointsRadius,
            pointsColor = pointsColor,
            lineWidth = lineWidth,
            lineColor = lineColor,
            areaColor = areaColor,
            composePaint = composePaint,
        )
    }

}

private fun DrawScope.xAxisDrawing(
    milestonesCounter: Int,
    initialValue: Int,
    step: Float,
    endHeight: Float,
    startWidth: Float,
    endWidth: Float,
    textSize: TextUnit,
    textColor: Color,
    milestonesColor: Color,
    axisColor: Color,
    nativePaint: Paint,
    composePaint: AndroidPaint,
) {

    val width = endWidth - startWidth

    // xAxisMilestonesCounter - 1 in order to remove last milestone space to the right
    val spaceBetweenMilestones = width / (milestonesCounter - 1)

    for (i in 0 until milestonesCounter) {
        val currentWidth = startWidth + i * spaceBetweenMilestones
        val milestoneText = (initialValue + i * step).toInt().toString()
        val textWidth = nativePaint.measureText(milestoneText)

        nativePaint.drawInColor(textColor) { paint ->
            paint.drawInTextSize(textSize.toPx()) {
                drawContext.canvas.nativeCanvas.drawText(
                    milestoneText,
                    currentWidth - textWidth / 2,
                    size.height,
                    it,
                )
            }
        }

        composePaint.drawInColor(milestonesColor) { paint ->
            drawContext.canvas.drawLine(
                Offset(currentWidth, endHeight),
                Offset(currentWidth, endHeight + Constants.milestonesMarkSize.toPx()),
                paint,
            )
        }
    }

    composePaint.drawInColor(axisColor) { paint ->
        drawContext.canvas.drawLine(
            Offset(startWidth, endHeight),
            Offset(endWidth, endHeight),
            paint,
        )
    }
}

private fun DrawScope.yAxisDrawing(
    yAxisMilestonesCounter: Int,
    initialValue: Int,
    step: Float,
    startWidth: Float,
    startHeight: Float,
    endHeight: Float,
    textSize: TextUnit,
    textColor: Color,
    milestonesColor: Color,
    axisColor: Color,
    nativePaint: Paint,
    composePaint: AndroidPaint,
) {
    val height = endHeight - startHeight

    val spaceBetweenMilestones = height / (yAxisMilestonesCounter - 1)

    for (i in 0 until yAxisMilestonesCounter) {
        val milestoneText = (initialValue + i * step).toInt().toString()

        val textHeight = nativePaint.getTextHeight(milestoneText)

        val currentHeight = endHeight - i * spaceBetweenMilestones

        nativePaint.drawInColor(textColor) { paint ->
            paint.drawInTextSize(textSize.toPx()) {
                drawContext.canvas.nativeCanvas.drawText(
                    "${(initialValue + i * step).toInt()}",
                    0f,
                    currentHeight + textHeight / 2,
                    it
                )
            }
        }

        composePaint.drawInColor(milestonesColor) { paint ->
            drawContext.canvas.drawLine(
                Offset(startWidth, currentHeight),
                Offset(startWidth - Constants.milestonesMarkSize.toPx(), currentHeight),
                paint,
            )
        }
    }

    composePaint.drawInColor(axisColor) { paint ->
        drawContext.canvas.drawLine(
            Offset(Constants.xAxisStartPadding.toPx(), startHeight),
            Offset(Constants.xAxisStartPadding.toPx(), endHeight),
            paint,
        )
    }
}

private fun DrawScope.dataDrawing(
    xAxisMilestonesCounter: Int,
    xStep: Float,
    initialXValue: Int,
    yAxisMilestonesCounter: Int,
    yStep: Float,
    initialYValue: Int,
    endDrawingWidth: Float,
    startDrawingWidth: Float,
    endDrawingHeight: Float,
    startDrawingHeight: Float,
    points: List<GraphData<Int>>,
    pointsRadius: Dp,
    pointsColor: Color,
    lineWidth: Dp,
    lineColor: Color,
    areaColor: List<Color>,
    composePaint: AndroidPaint,
) {
    val xMaxMilestone = (xAxisMilestonesCounter - 1) * xStep + initialXValue
    val yMaxMilestone = (yAxisMilestonesCounter - 1) * yStep + initialYValue

    val xPxPerUnit = (endDrawingWidth - startDrawingWidth) / (xMaxMilestone - initialXValue)
    val yPxPerUnit = (endDrawingHeight - startDrawingHeight) / (yMaxMilestone - initialYValue)

    for (i in points.indices) {

        val p0 = Offset(
            x = (points[i].xValue - initialXValue) * xPxPerUnit + startDrawingWidth,
            y = endDrawingHeight - (points[i].yValue - initialYValue) * yPxPerUnit,
        )
        val bezierPoints = points.getOrNull(i + 1)?.let { nextPoint ->

            val p3 = Offset(
                x = (nextPoint.xValue - initialXValue) * xPxPerUnit + startDrawingWidth,
                y = endDrawingHeight - (nextPoint.yValue - initialYValue) * yPxPerUnit,
            )

            val xBezierConnectionPoint = (p0.x + p3.x) / 2

            val p1 = Offset(
                x = xBezierConnectionPoint,
                y = p0.y,
            )

            val p2 = Offset(
                x = xBezierConnectionPoint,
                y = p3.y,
            )

            BezierPoints(p0 = p0, p1 = p1, p2 = p2, p3 = p3)
        }

        with(drawContext.canvas) {
            bezierPoints?.let { bezierPoints ->
                val path = Path().apply {
                    reset()
                    moveTo(p0.x, p0.y)
                    cubicTo(
                        bezierPoints.p1.x, bezierPoints.p1.y,
                        bezierPoints.p2.x, bezierPoints.p2.y,
                        bezierPoints.p3.x, bezierPoints.p3.y,
                    )
                }

                drawPath(
                    path,
                    color = lineColor,
                    style = Stroke(
                        width = lineWidth.toPx(),
                        cap = StrokeCap.Round
                    )
                )

                path.apply {
                    lineTo(bezierPoints.p3.x, endDrawingHeight)
                    lineTo(p0.x, endDrawingHeight)
                    close()
                }

                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(areaColor),
                )
            }

            composePaint.drawInColor(pointsColor) { composePaint ->
                drawCircle(
                    center = p0,
                    radius = pointsRadius.toPx(),
                    paint = composePaint,
                )
            }
        }
    }
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
            xTextSize = 4.sp,
            xTextColor = Color.Magenta,
            xMilestonesColor = Color.Green,
            xAxisColor = Color.Red,
            yTextSize = 8.sp,
            yTextColor = Color.Cyan,
            yMilestonesColor = Color.Blue,
            yAxisColor = Color.Green,
            pointsRadius = 4.dp,
            pointsColor = Color.Blue,
            lineWidth = 2.dp,
            lineColor = Color.Red,
            areaColor = listOf(Color.Cyan, Color.Yellow),
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.DarkGray)
                .padding(all = 2.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GraphBasePreview() {

    MaterialTheme {
        Graph(
            points = PreviewValues.points,
            initialXValue = 1,
            initialYValue = 10,
            xStep = 1f,
            yStep = 10f,
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White),
        )
    }
}

private object PreviewValues {
    val points by lazy {
        listOf(
            GraphData((1), 12),
            GraphData((2), 15),
            GraphData((3), 32),
            GraphData((4), 12),
            GraphData((5), 67),
            GraphData((6), 53),
            GraphData((7), 87),
            GraphData((8), 100),
            GraphData((9), 29),
            GraphData((10), 47),
            GraphData((11), 23),
            GraphData((12), 10),
            GraphData((13), 72),
            GraphData((14), 20),
            GraphData((15), 35),
            GraphData((16), 16),
            GraphData((17), 17),
            GraphData((18), 10),
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