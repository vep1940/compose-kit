package com.vep1940.compose.kit.graph

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
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
import com.vep1940.compose.kit.util.getTextHeight
import com.vep1940.compose.kit.util.withColor
import com.vep1940.compose.kit.util.withTextSize
import kotlin.math.ceil
import android.graphics.Paint as NativePaint


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
    xTextPadding: Dp = 0.dp,
    xMilestonesHeight: Dp = 1.dp,
    xMilestonesWidth: Dp = 1.dp,
    xMilestonesColor: Color = Color.Black,
    xMatrixColor: Color = Color.Black,
    xAxisColor: Color = Color.Black,
    yTextSize: TextUnit = 8.sp,
    yTextColor: Color = Color.Black,
    yTextPadding: Dp = 2.dp,
    yMilestonesHeight: Dp = 1.dp,
    yMilestonesWidth: Dp = 1.dp,
    yMilestonesColor: Color = Color.Black,
    yMatrixColor: Color = Color.Black,
    yAxisColor: Color = Color.Black,
    pointsRadius: Dp = 4.dp,
    pointsColor: Color = Color.Black,
    lineWidth: Dp = 1.dp,
    lineColor: Color = Color.Black,
    areaColor: List<Color> = listOf(Color.Transparent, Color.Transparent),
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

        val (xTextHeight, xTextWidth) = getTextSize(nativePaint, xTextSize.toPx(), xMax)
        val (yTextHeight, yTextWidth) = getTextSize(nativePaint, yTextSize.toPx(), yMax)

        val pointsRadiusPx = pointsRadius.toPx()

        val startDrawingWidth = yTextWidth + maxOf(
            yMilestonesWidth.toPx(),
            pointsRadiusPx
        ) + yTextPadding.toPx()
        val endDrawingWidth = size.width - maxOf((xTextWidth / 2), pointsRadiusPx)
        val startDrawingHeight = maxOf(yTextHeight / 2, pointsRadiusPx)
        val endDrawingHeight = size.height - (xTextHeight + maxOf(
            xMilestonesHeight.toPx(),
            pointsRadiusPx
        ) + xTextPadding.toPx())

        xAxisDrawing(
            milestonesCounter = xAxisMilestonesCounter,
            initialValue = initialXValue,
            step = xStep,
            startWidth = startDrawingWidth,
            startHeight = startDrawingHeight,
            endWidth = endDrawingWidth,
            endHeight = endDrawingHeight,
            textSize = xTextSize,
            textColor = xTextColor,
            milestonesHeight = xMilestonesHeight,
            milestonesWidth = xMilestonesWidth,
            milestonesColor = xMilestonesColor,
            matrixColor = xMatrixColor,
            axisColor = xAxisColor,
            nativePaint = nativePaint,
            composePaint = composePaint,
        )

        yAxisDrawing(
            milestonesCounter = yAxisMilestonesCounter,
            initialValue = initialYValue,
            step = yStep,
            startWidth = startDrawingWidth,
            startHeight = startDrawingHeight,
            endWidth = endDrawingWidth,
            endHeight = endDrawingHeight,
            textSize = yTextSize,
            textColor = yTextColor,
            milestonesHeight = yMilestonesHeight,
            milestonesWidth = yMilestonesWidth,
            milestonesColor = yMilestonesColor,
            matrixColor = yMatrixColor,
            axisColor = yAxisColor,
            nativePaint = nativePaint,
            composePaint = composePaint,
        )

        dataDrawing(
            xMilestonesCounter = xAxisMilestonesCounter,
            xStep = xStep,
            initialXValue = initialXValue,
            yMilestonesCounter = yAxisMilestonesCounter,
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

private fun getTextSize(
    nativePaint: Paint,
    textSizePx: Float,
    maxValue: Int,
): Pair<Float, Float> {
    var xTextHeight = 0f
    var xTextWidth = 0f
    nativePaint.withTextSize(textSizePx) {
        xTextHeight = nativePaint.fontMetrics.descent - nativePaint.fontMetrics.ascent
        xTextWidth = nativePaint.measureText(maxValue.toString())
    }
    return Pair(xTextHeight, xTextWidth)
}

private fun DrawScope.xAxisDrawing(
    milestonesCounter: Int,
    initialValue: Int,
    step: Float,
    startWidth: Float,
    startHeight: Float,
    endWidth: Float,
    endHeight: Float,
    textSize: TextUnit,
    textColor: Color,
    milestonesHeight: Dp,
    milestonesWidth: Dp,
    milestonesColor: Color,
    matrixColor: Color,
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

        nativePaint.withColor(textColor) { paint ->
            paint.withTextSize(textSize.toPx()) {
                drawContext.canvas.nativeCanvas.drawText(
                    milestoneText,
                    currentWidth - textWidth / 2,
                    size.height,
                    it,
                )
            }
        }
        composePaint.withColor(milestonesColor) { paint ->
            val milestonesHeightPx = milestonesHeight.toPx()
            val milestonesWidthPx = milestonesWidth.toPx()

            drawContext.canvas.drawRect(
                left = currentWidth - milestonesWidthPx / 2,
                top = endHeight,
                right = currentWidth + milestonesWidthPx / 2,
                bottom = endHeight + milestonesHeightPx,
                paint = paint,
            )
        }

        composePaint.withColor(matrixColor) { paint ->
            drawContext.canvas.drawLine(
                Offset(currentWidth, startHeight),
                Offset(currentWidth, endHeight),
                paint,
            )
        }
    }

    composePaint.withColor(axisColor) { paint ->
        drawContext.canvas.drawLine(
            Offset(startWidth, endHeight),
            Offset(endWidth, endHeight),
            paint,
        )
    }
}

private fun DrawScope.yAxisDrawing(
    milestonesCounter: Int,
    initialValue: Int,
    step: Float,
    startWidth: Float,
    startHeight: Float,
    endWidth: Float,
    endHeight: Float,
    textSize: TextUnit,
    textColor: Color,
    milestonesHeight: Dp,
    milestonesWidth: Dp,
    milestonesColor: Color,
    matrixColor: Color,
    axisColor: Color,
    nativePaint: Paint,
    composePaint: AndroidPaint,
) {
    val height = endHeight - startHeight

    val spaceBetweenMilestones = height / (milestonesCounter - 1)

    for (i in 0 until milestonesCounter) {
        val milestoneText = (initialValue + i * step).toInt().toString()

        val textHeight = nativePaint.getTextHeight(milestoneText)

        val currentHeight = endHeight - i * spaceBetweenMilestones

        nativePaint.withColor(textColor) { paint ->
            paint.withTextSize(textSize.toPx()) {
                drawContext.canvas.nativeCanvas.drawText(
                    "${(initialValue + i * step).toInt()}",
                    0f,
                    currentHeight + textHeight / 2,
                    it
                )
            }
        }

        composePaint.withColor(milestonesColor) { paint ->
            val milestonesHeightPx = milestonesHeight.toPx()
            val milestonesWidthPx = milestonesWidth.toPx()

            drawContext.canvas.drawRect(
                left = startWidth - milestonesWidthPx,
                top = currentHeight - milestonesHeightPx / 2,
                right = startWidth,
                bottom = currentHeight + milestonesHeightPx / 2,
                paint = paint,
            )
        }

        composePaint.withColor(matrixColor) { paint ->
            drawContext.canvas.drawLine(
                Offset(startWidth, currentHeight),
                Offset(endWidth, currentHeight),
                paint,
            )
        }
    }

    composePaint.withColor(axisColor) { paint ->
        drawContext.canvas.drawLine(
            Offset(startWidth, startHeight),
            Offset(startWidth, endHeight),
            paint,
        )
    }
}

private fun DrawScope.dataDrawing(
    xMilestonesCounter: Int,
    xStep: Float,
    initialXValue: Int,
    yMilestonesCounter: Int,
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
    val xMaxMilestone = (xMilestonesCounter - 1) * xStep + initialXValue
    val yMaxMilestone = (yMilestonesCounter - 1) * yStep + initialYValue

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

            composePaint.withColor(pointsColor) { composePaint ->
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
            xTextPadding = (-1).dp,
            xMilestonesHeight = 4.dp,
            xMilestonesWidth = 4.dp,
            xMilestonesColor = Color.Green,
            xMatrixColor = Color.Green,
            xAxisColor = Color.Red,
            yTextSize = 8.sp,
            yTextColor = Color.Cyan,
            yTextPadding = 1.dp,
            yMilestonesHeight = 2.dp,
            yMilestonesWidth = 2.dp,
            yMilestonesColor = Color.Magenta,
            yMatrixColor = Color.Red,
            yAxisColor = Color.Green,
            pointsRadius = 4.dp,
            pointsColor = Color.Blue,
            lineWidth = 2.dp,
            lineColor = Color.Red,
            areaColor = listOf(Color.Cyan, Color.Yellow),
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.DarkGray),
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
            GraphData((1), 100),
            GraphData((2), 15),
            GraphData((3), 32),
            GraphData((4), 10),
            GraphData((5), 67),
            GraphData((6), 53),
            GraphData((7), 87),
            GraphData((8), 100),
            GraphData((9), 29),
        )
    }
}