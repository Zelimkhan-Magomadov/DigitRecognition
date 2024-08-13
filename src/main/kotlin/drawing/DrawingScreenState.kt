package drawing

import usecase.Line

data class DrawingScreenState(
    val canvasLines: List<Line> = emptyList(),
    val predictedNumber: Int? = null
)