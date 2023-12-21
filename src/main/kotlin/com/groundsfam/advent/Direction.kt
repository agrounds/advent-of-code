package com.groundsfam.advent

import com.groundsfam.advent.points.Point

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    operator fun unaryMinus(): Direction = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }

    val cw: Direction get() = when (this) {
        UP -> RIGHT
        RIGHT -> DOWN
        DOWN -> LEFT
        LEFT -> UP
    }
    val ccw: Direction get() = when (this) {
        UP -> LEFT
        LEFT -> DOWN
        DOWN -> RIGHT
        RIGHT -> UP
    }

    fun isVertical(): Boolean = this == UP || this == DOWN
    fun isHorizontal(): Boolean = !this.isVertical()
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
