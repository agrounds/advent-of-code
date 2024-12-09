package com.groundsfam.advent.points

import com.groundsfam.advent.Direction
import com.groundsfam.advent.asPoint

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun minus(other: Point) = Point(x - other.x, y - other.y)
    operator fun times(factor: Int) = Point(x * factor, y * factor)
    operator fun div(factor: Int) = Point(x / factor, y / factor)
}

operator fun Int.times(p: Point) = p * this

// north, south, east, west, and diagonals
val Point.n get() = Point(x, y - 1)
val Point.s get() = Point(x, y + 1)
val Point.e get() = Point(x + 1, y)
val Point.w get() = Point(x - 1, y)
val Point.nw get() = Point(x - 1, y - 1)
val Point.ne get() = Point(x + 1, y - 1)
val Point.sw get() = Point(x - 1, y + 1)
val Point.se get() = Point(x + 1, y + 1)

// up, down, left and right directions
val Point.up get() = this.n
val Point.down get() = this.s
val Point.left get() = this.w
val Point.right get() = this.e

fun Point.adjacents(diagonal: Boolean = true): List<Point> =
    if (diagonal) listOf(n, nw, w, sw, s, se, e, ne)
    else listOf(n, w, s, e)

fun Point.go(d: Direction): Point = this + d.asPoint()

fun Iterable<Point>.sum(): Point = this.fold(Point(0, 0)) { sum, point ->
    sum + point
}

data class Point3(val x: Int, val y: Int, val z: Int) {
    operator fun plus(other: Point3) = Point3(x + other.x, y + other.y, z + other.z)
}

fun Point3.adjacents(): List<Point3> = listOf(
    Point3(1, 0, 0) + this,
    Point3(-1, 0, 0) + this,
    Point3(0, 1, 0) + this,
    Point3(0, -1, 0) + this,
    Point3(0, 0, 1) + this,
    Point3(0, 0, -1) + this,
)
