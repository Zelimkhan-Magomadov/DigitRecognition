package usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.awt.BasicStroke
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

class CanvasToImageUseCase {
    suspend operator fun invoke(
        lines: List<Line>,
        width: Int,
        height: Int,
        outputSize: Int
    ): BufferedImage {
        return withContext(Dispatchers.Default) {
            // Создание BufferedImage и Graphics2D с черным фоном
            val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
            val graphics2D = bufferedImage.createGraphics()
            graphics2D.color = java.awt.Color.BLACK
            graphics2D.fillRect(0, 0, width, height)

            // Установка белого цвета для линий и толщины линии
            graphics2D.color = java.awt.Color.WHITE
            graphics2D.stroke = BasicStroke(26f)  // Толщина линии

            // Рисование всех линий на graphics2D
            lines.forEach { line ->
                graphics2D.drawLine(
                    line.startX.toInt(),
                    line.startY.toInt(),
                    line.endX.toInt(),
                    line.endY.toInt()
                )
            }

            graphics2D.dispose()

            val centeredBufferedImage = centerImage(bufferedImage)

            // ImageIO.write(centeredBufferedImage, "png", File("src/main/resources/imageFromCanvas.png"))

            // Изменение размера изображения до 28x28
            resizeBufferedImage(centeredBufferedImage, outputSize, outputSize)
        }
    }

    private suspend fun centerImage(originalImage: BufferedImage): BufferedImage {
        val width = originalImage.width
        val height = originalImage.height

        // Инициализация границ изображения
        var minX = width
        var minY = height
        var maxX = 0
        var maxY = 0

        // Проход по каждому пикселю и определение границ нарисованного изображения
        for (y in 0 until height) {
            for (x in 0 until width) {
                val pixel = originalImage.getRGB(x, y)
                val red = (pixel shr 16) and 0xFF
                val green = (pixel shr 8) and 0xFF
                val blue = pixel and 0xFF
                val gray = (0.299 * red + 0.587 * green + 0.114 * blue).toInt()

                if (gray != 0) { // Если пиксель не черный
                    if (x < minX) minX = x
                    if (y < minY) minY = y
                    if (x > maxX) maxX = x
                    if (y > maxY) maxY = y
                }
            }
        }

        // Определение размеров нарисованного изображения
        val drawnWidth = maxX - minX + 1
        val drawnHeight = maxY - minY + 1

        // Создание нового изображения, где нарисованное будет в центре
        val centeredImage = BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY)
        val graphics = centeredImage.createGraphics()

        // Координаты для рисования изображения в центре
        val xOffset = (width - drawnWidth) / 2
        val yOffset = (height - drawnHeight) / 2

        // Вырезаем нарисованное изображение и вставляем его в центр нового изображения
        graphics.drawImage(originalImage.getSubimage(minX, minY, drawnWidth, drawnHeight), xOffset, yOffset, null)
        graphics.dispose()

        return centeredImage
    }

    private suspend fun resizeBufferedImage(image: BufferedImage, newWidth: Int, newHeight: Int): BufferedImage {
        val resizedImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
        val g2d: Graphics2D = resizedImage.createGraphics()

        // Используем билинейную интерполяцию и высокое качество рендеринга
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR)
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)

        g2d.drawImage(image, 0, 0, newWidth, newHeight, null)
        g2d.dispose()

        // ImageIO.write(resizedImage, "png", File("src/main/resources/resizedImage.png"))
        return resizedImage
    }
}