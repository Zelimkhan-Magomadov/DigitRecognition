package drawing

import usecase.Line

sealed interface DrawingScreenIntent {
    data class AddLine(val line: Line) : DrawingScreenIntent
    data object ClearLines : DrawingScreenIntent
    data class PredictNumber(val width: Int, val height: Int) : DrawingScreenIntent
}