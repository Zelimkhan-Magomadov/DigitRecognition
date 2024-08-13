package drawing

import usecase.Line
import extensions.toFloatArray
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import neural_network.DigitRecognizer
import usecase.CanvasToImageUseCase
import java.io.Closeable

class DrawingScreenViewModel(
    private val digitRecognizer: DigitRecognizer,
    private val canvasToImageUseCase: CanvasToImageUseCase,
) : Closeable {
    private val viewModelJob = Job()
    private val viewModelScope = CoroutineScope(viewModelJob)

    private val _state = MutableStateFlow(DrawingScreenState())
    val state = _state.asStateFlow()

    fun processIntent(intent: DrawingScreenIntent) {
        viewModelScope.launch {
            when (intent) {
                is DrawingScreenIntent.AddLine -> addLine(intent.line)
                DrawingScreenIntent.ClearLines -> clearLines()
                is DrawingScreenIntent.PredictNumber -> predictNumber(intent.width, intent.height)
            }
        }
    }

    private fun addLine(line: Line) {
        _state.update {
            it.copy(canvasLines = it.canvasLines + line)
        }
    }

    private fun clearLines() {
        _state.update {
            it.copy(
                canvasLines = emptyList(),
                predictedNumber = null
            )
        }
    }

    private suspend fun predictNumber(width: Int, height: Int) {
        val canvasImage = canvasToImageUseCase(
            lines = state.value.canvasLines,
            width = width,
            height = height,
            outputSize = 28
        )
        val predictedNumber = digitRecognizer.predict(canvasImage.toFloatArray())
        _state.update { it.copy(predictedNumber = predictedNumber.toInt()) }
    }

    override fun close() {
        viewModelJob.cancel()
    }
}