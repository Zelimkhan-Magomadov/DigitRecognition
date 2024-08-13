package neural_network

import org.jetbrains.kotlinx.dl.api.core.SavingFormat
import org.jetbrains.kotlinx.dl.api.core.Sequential
import org.jetbrains.kotlinx.dl.api.core.WritingMode
import org.jetbrains.kotlinx.dl.api.core.activation.Activations
import org.jetbrains.kotlinx.dl.api.core.initializer.HeNormal
import org.jetbrains.kotlinx.dl.api.core.layer.convolutional.Conv2D
import org.jetbrains.kotlinx.dl.api.core.layer.convolutional.ConvPadding
import org.jetbrains.kotlinx.dl.api.core.layer.core.Dense
import org.jetbrains.kotlinx.dl.api.core.layer.core.Input
import org.jetbrains.kotlinx.dl.api.core.layer.pooling.MaxPool2D
import org.jetbrains.kotlinx.dl.api.core.layer.reshaping.Flatten
import org.jetbrains.kotlinx.dl.api.core.loss.Losses
import org.jetbrains.kotlinx.dl.api.core.metric.Metrics
import org.jetbrains.kotlinx.dl.api.core.optimizer.Adam
import org.jetbrains.kotlinx.dl.dataset.Dataset
import java.io.File

class DigitRecognizer(
    imageSize: Long = 28L,
    numChannels: Long = 1L,
    numLabels: Int = 10,
    heNormal: HeNormal = HeNormal(),
) {
    private var model: Sequential = Sequential.of(
        Input(
            imageSize,
            imageSize,
            numChannels
        ),
        Conv2D(
            filters = 32,
            kernelSize = 3,
            strides = 1,
            activation = Activations.Relu,
            kernelInitializer = heNormal,
            biasInitializer = heNormal,
            padding = ConvPadding.SAME
        ),
        MaxPool2D(
            poolSize = 2,
            strides = 2,
            padding = ConvPadding.SAME
        ),
        Conv2D(
            filters = 64,
            kernelSize = 3,
            strides = 1,
            activation = Activations.Relu,
            kernelInitializer = heNormal,
            biasInitializer = heNormal,
            padding = ConvPadding.SAME
        ),
        MaxPool2D(
            poolSize = 2,
            strides = 2,
            padding = ConvPadding.SAME
        ),
        Conv2D(
            filters = 128,
            kernelSize = 3,
            strides = 1,
            activation = Activations.Relu,
            kernelInitializer = heNormal,
            biasInitializer = heNormal,
            padding = ConvPadding.SAME
        ),
        Conv2D(
            filters = 128,
            kernelSize = 3,
            strides = 1,
            activation = Activations.Relu,
            kernelInitializer = heNormal,
            biasInitializer = heNormal,
            padding = ConvPadding.SAME
        ),
        MaxPool2D(
            poolSize = 2,
            strides = 2,
            padding = ConvPadding.SAME
        ),
        Conv2D(
            filters = 256,
            kernelSize = 3,
            strides = 1,
            activation = Activations.Relu,
            kernelInitializer = heNormal,
            biasInitializer = heNormal,
            padding = ConvPadding.SAME
        ),
        Conv2D(
            filters = 256,
            kernelSize = 3,
            strides = 1,
            activation = Activations.Relu,
            kernelInitializer = heNormal,
            biasInitializer = heNormal,
            padding = ConvPadding.SAME
        ),
        MaxPool2D(
            poolSize = 2,
            strides = 2,
            padding = ConvPadding.SAME
        ),
        Conv2D(
            filters = 128,
            kernelSize = 3,
            strides = 1,
            activation = Activations.Relu,
            kernelInitializer = heNormal,
            biasInitializer = heNormal,
            padding = ConvPadding.SAME
        ),
        Conv2D(
            filters = 128,
            kernelSize = 3,
            strides = 1,
            activation = Activations.Relu,
            kernelInitializer = heNormal,
            biasInitializer = heNormal,
            padding = ConvPadding.SAME
        ),
        MaxPool2D(
            poolSize = 2,
            strides = 2,
            padding = ConvPadding.SAME
        ),
        Flatten(),
        Dense(
            outputSize = 2048,
            activation = Activations.Relu,
            kernelInitializer = heNormal,
            biasInitializer = heNormal
        ),
        Dense(
            outputSize = 1000,
            activation = Activations.Relu,
            kernelInitializer = heNormal,
            biasInitializer = heNormal
        ),
        Dense(
            outputSize = numLabels,
            activation = Activations.Linear,
            kernelInitializer = heNormal,
            biasInitializer = heNormal
        )
    )

    fun train(
        trainData: Dataset,
        testData: Dataset,
        trainingBatchSize: Int = 200,
        testBatchSize: Int = 1000,
        epochs: Int = 1,
        modelSavePath: String? = null
    ) {
        model.use {
            model.compileModel()
            model.init()
            model.fit(
                dataset = trainData,
                epochs = epochs,
                batchSize = trainingBatchSize
            )
            val accuracy = it.evaluate(
                dataset = testData,
                batchSize = testBatchSize
            ).metrics[Metrics.ACCURACY]

            println("Accuracy: $accuracy")

            if (modelSavePath != null) {
                saveModel(modelSavePath)
            }
        }
    }

    private fun saveModel(modelSavePath: String) {
        model.save(
            modelDirectory = File(modelSavePath),
            savingFormat = SavingFormat.JSON_CONFIG_CUSTOM_VARIABLES,
            writingMode = WritingMode.OVERRIDE
        )
    }

    fun loadModel(modelSavedPath: String) {
        val savedModel = File(modelSavedPath)
        Sequential.loadDefaultModelConfiguration(savedModel).apply {
            compileModel()
            loadWeights(savedModel)
            model = this
        }
    }

    private fun Sequential.compileModel() {
        this.compile(
            optimizer = Adam(),
            loss = Losses.SOFT_MAX_CROSS_ENTROPY_WITH_LOGITS,
            metric = Metrics.ACCURACY
        )
    }

    fun predict(input: FloatArray): Int {
        return model.predict(input)
    }
}