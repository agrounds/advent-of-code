package advent

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT,
}
fun Direction.asPoint() = when (this) {
    Direction.UP -> Point(0, -1)
    Direction.DOWN -> Point(0, 1)
    Direction.LEFT -> Point(-1, 0)
    Direction.RIGHT -> Point(1, 0)
}

fun Char.toDirection(): Direction? = when (this) {
    '^' -> Direction.UP
    'v' -> Direction.DOWN
    '>' -> Direction.RIGHT
    '<' -> Direction.LEFT
    else -> null
}
