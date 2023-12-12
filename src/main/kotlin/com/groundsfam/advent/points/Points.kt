package com.groundsfam.advent.points

data class Point(val x: Int, val y: Int) {
    operator fun plus(other: Point) = Point(x + other.x, y + other.y)
    operator fun times(factor: Int) = Point(x * factor, y * factor)
}
// north south, east, west, and diagonals
val Point.n get() = Point(x, y - 1)
val Point.s get() = Point(x, y + 1)
val Point.e get() = Point(x + 1, y)
val Point.w get() = Point(x - 1, y)
val Point.nw get() = Point(x - 1, y - 1)
val Point.ne get() = Point(x + 1, y - 1)
val Point.sw get() = Point(x - 1, y + 1)
val Point.se get() = Point(x + 1, y + 1)

fun Iterable<Point>.sum(): Point = this.fold(Point(0, 0)) { sum, point ->
    sum + point
}

operator fun <T> List<List<T>>.get(p: Point) =
    this[p.y][p.x]
operator fun <T> MutableList<MutableList<T>>.set(p: Point, v: T) {
    this[p.y][p.x] = v
}
val <T> List<List<T>>.pointIndices: Set<Point>
    get() = this.flatMapIndexedTo(mutableSetOf()) { y, row ->
        row.indices.map { x -> Point(x, y) }
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