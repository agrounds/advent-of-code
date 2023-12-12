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

fun Point.go(d: Direction): Point = this + d.asPoint()
