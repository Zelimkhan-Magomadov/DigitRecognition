package drawing

import usecase.Line
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DrawingScreen(
    modifier: Modifier = Modifier,
    state: DrawingScreenState = DrawingScreenState(),
    onIntent: (DrawingScreenIntent) -> Unit = {}
) {
    var size by remember { mutableStateOf(Size(0f, 0f)) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            repeat(10) { times ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = times.toString(), fontSize = 24.sp)
                    Spacer(modifier.height(8.dp))
                    Box(
                        modifier = Modifier.size(24.dp).background(
                            color = if (state.predictedNumber == times) Color.Green else Color.Gray,
                            shape = CircleShape
                        )
                    )
                }
            }
        }

        Box(modifier = Modifier.weight(1f)) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                onIntent(
                                    DrawingScreenIntent.PredictNumber(
                                        size.width.toInt(),
                                        size.height.toInt()
                                    )
                                )
                            }
                        ) { change, dragAmount ->
                            change.consume()

                            val line = Line(
                                startX = change.position.x - dragAmount.x,
                                startY = change.position.y - dragAmount.y,
                                endX = change.position.x,
                                endY = change.position.y
                            )

                            onIntent(DrawingScreenIntent.AddLine(line))
                        }
                    }
            ) {
                size = this.size
                state.canvasLines.forEach { line ->
                    drawLine(
                        color = Color(line.color),
                        start = Offset(line.startX, line.startY),
                        end = Offset(line.endX, line.endY),
                        strokeWidth = line.strokeWidth,
                        cap = StrokeCap.Round
                    )
                }
            }
        }

        Button(
            onClick = { onIntent(DrawingScreenIntent.ClearLines) },
            enabled = state.canvasLines.isNotEmpty()
        ) {
            Text(text = "Очистить")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
@Preview
private fun Preview() {
    MaterialTheme {
        DrawingScreen()
    }
}