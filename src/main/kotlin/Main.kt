import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import drawing.DrawingScreen
import drawing.DrawingScreenViewModel
import neural_network.DigitRecognizer
import org.jetbrains.kotlinx.dl.dataset.embedded.mnist
import usecase.CanvasToImageUseCase
import java.io.File

private const val DIGIT_RECOGNIZE_MODEL_PATH = "src/main/resources/digit_recognise_model"

private fun trainNeuralNetwork() {
    val (train, test) = mnist(cacheDirectory = File("src/main/resources"))
    val digitRecognizer = DigitRecognizer()
    digitRecognizer.train(
        trainData = train,
        testData = test,
        epochs = 10,
        modelSavePath = DIGIT_RECOGNIZE_MODEL_PATH
    )
}

@Composable
@Preview
fun App() {
    val viewModel = DrawingScreenViewModel(
        digitRecognizer = DigitRecognizer().apply { loadModel(DIGIT_RECOGNIZE_MODEL_PATH) },
        canvasToImageUseCase = CanvasToImageUseCase()
    )

    MaterialTheme {
        DrawingScreen(
            state = viewModel.state.collectAsState().value,
            onIntent = viewModel::processIntent
        )
    }
}

fun main() = application {
    // trainNeuralNetwork()

    Window(
        onCloseRequest = ::exitApplication,
        title = "DigitRecognition",
        icon = painterResource("logo.png")
    ) {
        App()
    }
}