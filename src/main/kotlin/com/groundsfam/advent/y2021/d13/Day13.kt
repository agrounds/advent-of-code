package com.groundsfam.advent.y2021.d13

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.min


// e.g. y=3 corresponds to Fold(along=3, isHorizontal=true)
private data class Fold(val along: Int, val isHorizontal: Boolean)

private class Solution(originalPoints: Set<Point>, private val folds: List<Fold>) {
    private var points = originalPoints
    private var foldIdx = 0

    fun fold() {
        val (foldAlong, foldIsHorizontal) = folds[foldIdx]
        points = points.mapTo(mutableSetOf()) { (x, y) ->
            if (foldIsHorizontal) {
                Point(x, min(y, 2 * foldAlong - y))
            } else {
                Point(min(x, 2 * foldAlong - x), y)
            }
        }
        foldIdx++
    }

    fun doAllFolds() {
        repeat(folds.size - foldIdx) {
            fold()
        }
    }

    val numPoints get() = points.size

    fun drawPaper() {
        val maxX = points.maxOf { it.x }
        val maxY = points.maxOf { it.y }
        (0..maxY).forEach { y ->
            (0..maxX).forEach { x ->
                if (Point(x, y) in points) print("#")
                else print(".")
            }
            println()
        }
    }
}

fun main() = timed {
    val solution = (DATAPATH / "2021/day13.txt").useLines { lines ->
        val points = mutableSetOf<Point>()
        val folds = mutableListOf<Fold>()
        var readPoints = true

        lines.forEach { line ->
            when {
                line.isBlank() -> {
                    readPoints = false
                }
                readPoints -> {
                    val (x, y) = line.split(",")
                    points.add(Point(x.toInt(), y.toInt()))
                }
                else -> {
                    val (direction, along) = line.substring(11).split("=")
                    folds.add(Fold(along.toInt(), direction == "y"))
                }
            }
        }
        Solution(points, folds)
    }

    solution.fold()
    println("Part one: ${solution.numPoints}")
    solution.doAllFolds()
    println("\nPart two:")
    solution.drawPaper()
}
