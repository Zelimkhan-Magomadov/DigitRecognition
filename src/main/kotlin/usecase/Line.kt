package usecase

data class Line(
    val startX: Float,
    val startY: Float,
    val endX: Float,
    val endY: Float,
    val color: Long = 0xFF000000,
    val strokeWidth: Float = 12f
)