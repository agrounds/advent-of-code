package com.groundsfam.advent.y2021.d05

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.points.Point
import kotlin.io.path.div
import kotlin.io.path.useLines


fun range(from: Point, to: Point): List<Point> {
    val xStep = when {
        from.x < to.x -> 1
        from.x > to.x -> -1
        else -> 0
    }
    val yStep = when {
        from.y < to.y -> 1
        from.y > to.y -> -1
        else -> 0
    }

    return mutableListOf<Point>().apply {
        var p = from
        add(p)
        while (p != to) {
            p += Point(xStep, yStep)
            add(p)
        }
    }
}

fun findOverlaps(ventLines: List<Pair<Point, Point>>, includeDiagonals: Boolean): Int {
    val ventPoints = mutableSetOf<Point>()
    val overlapPoints = mutableSetOf<Point>()

    ventLines.forEach { (from, to) ->
        if (includeDiagonals || from.x == to.x || from.y == to.y) {
            range(from, to).forEach { point ->
                if (!ventPoints.add(point)) {
                    overlapPoints.add(point)
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
    println("Part one: ${findOverlaps(ventLines, false)}")
    println("Part two: ${findOverlaps(ventLines, true)}")
}
