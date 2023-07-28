package com.vep1940.compose.kit.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil


@OptIn(ExperimentalTextApi::class)
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
    pointsRadius: Dp = 2.dp,
    pointsColor: Color = Color.Black,
    lineWidth: Dp = 1.dp,
    lineColor: Color = Color.Black,
    areaColor: List<Color> = listOf(Color.Transparent, Color.Transparent),
) {
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .fillMaxSize()
    ) {

        val xMax = points.maxOf { it.xValue }
        val yMax = points.maxOf { it.yValue }

        // maxValue - initialValue plus 1 in order to add the initialValue to the difference
        val xMilestonesCounter = (ceil((xMax - initialXValue) / xStep) + 1).toInt()
        val yMilestonesCounter = (ceil((yMax - initialYValue) / yStep) + 1).toInt()

        val xTextStyle = TextStyle(
            color = xTextColor,
            fontSize = xTextSize,
        )
        val yTextStyle = TextStyle(
            color = yTextColor,
            fontSize = yTextSize,
        )

        val xMeasuredTextSize = textMeasurer.measure(
            text = ((xMilestonesCounter - 1) * xStep + initialXValue).toInt().toString(),
            style = xTextStyle,
        ).size
        val yMeasuredTextSize = textMeasurer.measure(
            text = ((yMilestonesCounter - 1) * yStep + initialYValue).toInt().toString(),
            style = yTextStyle,
        ).size

        val pointsRadiusPx = pointsRadius.toPx()

        val startDrawingWidth = yMeasuredTextSize.width + maxOf(
            yMilestonesWidth.toPx(),
            pointsRadiusPx
        ) + yTextPadding.toPx()
        val endDrawingWidth = size.width - maxOf((xMeasuredTextSize.width / 2f), pointsRadiusPx)
        val startDrawingHeight = maxOf(yMeasuredTextSize.height / 2f, pointsRadiusPx)
        val endDrawingHeight = size.height - (xMeasuredTextSize.height + maxOf(
            xMilestonesHeight.toPx(),
            pointsRadiusPx
        ) + xTextPadding.toPx())

        xAxisDrawing(
            milestonesCounter = xMilestonesCounter,
            initialValue = initialXValue,
            step = xStep,
            startWidth = startDrawingWidth,
            startHeight = startDrawingHeight,
            endWidth = endDrawingWidth,
            endHeight = endDrawingHeight,
            textMeasurer = textMeasurer,
            textStyle = xTextStyle,
            milestonesHeight = xMilestonesHeight,
            milestonesWidth = xMilestonesWidth,
            milestonesColor = xMilestonesColor,
            matrixColor = xMatrixColor,
            axisColor = xAxisColor,
        )

        yAxisDrawing(
            milestonesCounter = yMilestonesCounter,
            initialValue = initialYValue,
            step = yStep,
            startWidth = startDrawingWidth,
            startHeight = startDrawingHeight,
            endWidth = endDrawingWidth,
            endHeight = endDrawingHeight,
            textMeasurer = textMeasurer,
            measuredTextSize = yMeasuredTextSize,
            textStyle = yTextStyle,
            milestonesHeight = yMilestonesHeight,
            milestonesWidth = yMilestonesWidth,
            milestonesColor = yMilestonesColor,
            matrixColor = yMatrixColor,
            axisColor = yAxisColor,
        )

        dataDrawing(
            xMilestonesCounter = xMilestonesCounter,
            xStep = xStep,
            initialXValue = initialXValue,
            yMilestonesCounter = yMilestonesCounter,
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
        )
    }

}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.xAxisDrawing(
    milestonesCounter: Int,
    initialValue: Int,
    step: Float,
    startWidth: Float,
    startHeight: Float,
    endWidth: Float,
    endHeight: Float,
    textMeasurer: TextMeasurer,
    textStyle: TextStyle,
    milestonesHeight: Dp,
    milestonesWidth: Dp,
    milestonesColor: Color,
    matrixColor: Color,
    axisColor: Color,
) {

    val width = endWidth - startWidth

    // milestonesCounter - 1 in order to remove last milestone space to the right
    val spaceBetweenMilestones = width / (milestonesCounter - 1)

    for (i in 0 until milestonesCounter) {
        val currentWidth = startWidth + i * spaceBetweenMilestones
        val milestoneText = (initialValue + i * step).toInt().toString()

        val textMeasuredSize = textMeasurer.measure(
            text = milestoneText,
            style = textStyle,
        ).size

        drawText(
            textMeasurer = textMeasurer,
            text = milestoneText,
            style = textStyle,
            topLeft = Offset(
                x = currentWidth - textMeasuredSize.width / 2,
                y = size.height - textMeasuredSize.height
            )
        )

        val milestonesWidthPx = milestonesWidth.toPx()
        val milestonesHeightPx = milestonesHeight.toPx()

        drawRect(
            color = milestonesColor,
            topLeft = Offset(
                x = currentWidth - milestonesWidthPx / 2,
                y = endHeight,
            ),
            size = Size(
                width = milestonesWidthPx,
                height = milestonesHeightPx,
            )
        )

        drawLine(
            color = matrixColor,
            start = Offset(currentWidth, startHeight),
            end = Offset(currentWidth, endHeight),
        )
    }

    drawLine(
        color = axisColor,
        start = Offset(startWidth, endHeight),
        end = Offset(endWidth, endHeight),
    )
}

@OptIn(ExperimentalTextApi::class)
private fun DrawScope.yAxisDrawing(
    milestonesCounter: Int,
    initialValue: Int,
    step: Float,
    startWidth: Float,
    startHeight: Float,
    endWidth: Float,
    endHeight: Float,
    textMeasurer: TextMeasurer,
    measuredTextSize: IntSize,
    textStyle: TextStyle,
    milestonesHeight: Dp,
    milestonesWidth: Dp,
    milestonesColor: Color,
    matrixColor: Color,
    axisColor: Color,
) {
    val height = endHeight - startHeight

    val spaceBetweenMilestones = height / (milestonesCounter - 1)

    for (i in 0 until milestonesCounter) {
        val currentHeight = endHeight - i * spaceBetweenMilestones

        drawText(
            textMeasurer = textMeasurer,
            text = "${(initialValue + i * step).toInt()}",
            style = textStyle,
            topLeft = Offset(
                x = 0f,
                y = currentHeight - measuredTextSize.height / 2
            ),
        )

        val milestonesWidthPx = milestonesWidth.toPx()
        val milestonesHeightPx = milestonesHeight.toPx()

        drawRect(
            color = milestonesColor,
            topLeft = Offset(
                x = startWidth - milestonesWidthPx,
                y = currentHeight - milestonesHeightPx / 2,
            ),
            size = Size(
                width = milestonesWidthPx,
                height = milestonesHeightPx,
            )
        )

        drawLine(
            color = matrixColor,
            start = Offset(startWidth, currentHeight),
            end = Offset(endWidth, currentHeight),
        )

    }

    drawLine(
        color = axisColor,
        start = Offset(startWidth, startHeight),
        end = Offset(startWidth, endHeight),
    )

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
) {
    val xMaxMilestone = (xMilestonesCounter - 1) * xStep + initialXValue
    val yMaxMilestone = (yMilestonesCounter - 1) * yStep + initialYValue

    val xPxPerUnit = (endDrawingWidth - startDrawingWidth) / (xMaxMilestone - initialXValue)
    val yPxPerUnit = (endDrawingHeight - startDrawingHeight) / (yMaxMilestone - initialYValue)

    val dataPoints = mutableListOf<Offset>()
    val linePath = Path()
    val areaPath = Path()

    for (i in points.indices) {

        val p0 = Offset(
            x = (points[i].xValue - initialXValue) * xPxPerUnit + startDrawingWidth,
            y = endDrawingHeight - (points[i].yValue - initialYValue) * yPxPerUnit,
        )

        dataPoints.add(p0)

        points.getOrNull(i + 1)?.let { nextPoint ->

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

            val path = Path().apply {
                moveTo(p0.x, p0.y)
                cubicTo(
                    p1.x, p1.y,
                    p2.x, p2.y,
                    p3.x, p3.y,
                )
            }

            linePath.addPath(path = path)

            path.apply {
                lineTo(p3.x, endDrawingHeight)
                lineTo(p0.x, endDrawingHeight)
                close()
            }

            areaPath.addPath(path = path)
        }
    }

    drawPath(
        path = areaPath,
        brush = Brush.verticalGradient(areaColor),
    )

    drawPath(
        path = linePath,
        color = lineColor,
        style = Stroke(
            width = lineWidth.toPx(),
            cap = StrokeCap.Round
        )
    )

    dataPoints.forEach { point ->
        drawCircle(
            center = point,
            radius = pointsRadius.toPx(),
            color = pointsColor,
        )
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