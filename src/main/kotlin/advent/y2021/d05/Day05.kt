package advent.y2021.d05

import advent.DATAPATH
import advent.Point
import kotlin.io.path.div
import kotlin.io.path.useLines


fun findOverlaps(ventLines: List<Pair<Point, Point>>): Int {
    val ventPoints = mutableSetOf<Point>()
    val overlapPoints = mutableSetOf<Point>()

    ventLines.forEach { (from, to) ->
        when {
            from.x == to.x -> {
                val x = from.x
                val (minY, maxY) = listOf(from.y, to.y).sorted()
                (minY..maxY).forEach { y ->
                    if (!ventPoints.add(Point(x, y))) {
                        overlapPoints.add(Point(x, y))
                    }
                }
            }
            from.y == to.y -> {
                val y = from.y
                val (minX, maxX) = listOf(from.x, to.x).sorted()
                (minX..maxX).forEach { x ->
                    if (!ventPoints.add(Point(x, y))) {
                        overlapPoints.add(Point(x, y))
                    }
                }
            }
        }
    }

    return overlapPoints.size
}

fun main() {
    val ventLines = (DATAPATH / "2021/day05.txt").useLines { lines ->
        lines.toList().map { line ->
            val (first, _, second) = line.split(" ")
            fun String.toPoint() = this.split(",").map { it.toInt() }.let { (x, y) -> Point(x, y) }
            first.toPoint() to second.toPoint()
        }
    }
    println("Part one: ${findOverlaps(ventLines)}")
}
