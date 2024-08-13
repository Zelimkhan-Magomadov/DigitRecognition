package extensions

import java.awt.image.BufferedImage

fun BufferedImage.toFloatArray(): FloatArray {
    val width = this.width
    val height = this.height
    val floatArray = FloatArray(width * height)

    for (y in 0 until height) {
        for (x in 0 until width) {
            // Получаем цвет пикселя
            val pixel = this.getRGB(x, y)

            // Извлекаем компоненты цвета
            val red = (pixel shr 16) and 0xFF
            val green = (pixel shr 8) and 0xFF
            val blue = pixel and 0xFF

            // Преобразуем цвет в уровень серого
            val gray = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()

            // Нормализуем значение в диапазоне [0.0, 1.0]
            // Черный фон -> 0.0, Белая цифра -> 1.0
            floatArray[y * width + x] = gray / 255.0f
        }
    }

    return floatArray
}