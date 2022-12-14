package advent.y2022.d14

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

data class Point(val x: Int, val y: Int)
fun String.toPoint(): Point = split(",").let { (a, b) ->
    Point(a.toInt(), b.toInt())
}

class Solver(input: Sequence<String>) {
    private val blockedPoints: Set<Point>
    private val maxDepth: Int

    init {
        val tmpBlockedPoints = mutableSetOf<Point>()
        var tmpMaxDepth = 0
        input.forEach { line ->
            var prevPoint: Point? = null
            line.split(" -> ")
                .map { it.toPoint() }
                .forEach { point ->
                    tmpMaxDepth = maxOf( tmpMaxDepth, point.y)
                    val prevPoint1 = prevPoint
                    if (prevPoint1 != null) {
                        val fromX = minOf(point.x, prevPoint1.x)
                        val toX = maxOf(point.x, prevPoint1.x)
                        val fromY = minOf(point.y, prevPoint1.y)
                        val toY = maxOf(point.y, prevPoint1.y)
                        (fromX..toX).forEach { x ->
                            (fromY..toY).forEach { y ->
                                tmpBlockedPoints.add(Point(x, y))
                            }
                        }
                    }
                    prevPoint = point
                }
        }
        blockedPoints = tmpBlockedPoints
        maxDepth = tmpMaxDepth
    }

    fun simulateFallingSand(sandSource: Point, partOne: Boolean): Int {
        val bp = blockedPoints.toMutableSet()

        fun isBlocked(point: Point): Boolean =
            if (partOne) point in bp
            else point in bp || point.y >= maxDepth + 2

        fun simulateOne(): Boolean {
            var sand = sandSource
            while (true) {
                if (partOne && sand.y > maxDepth) return false
                if (!partOne && sandSource in bp) return false
                when {
                    !isBlocked(sand.copy(y = sand.y + 1)) -> {
                        sand = sand.copy(y = sand.y + 1)
                    }

                    !isBlocked(Point(sand.x - 1, sand.y + 1)) -> {
                        sand = Point(sand.x - 1, sand.y + 1)
                    }

                    !isBlocked(Point(sand.x + 1, sand.y + 1)) -> {
                        sand = Point(sand.x + 1, sand.y + 1)
                    }

                    else -> {
                        bp.add(sand)
                        return true
                    }
                }
            }
        }

        var sandAtRest = 0
        while (simulateOne()) sandAtRest++
        return sandAtRest
    }
}



fun main() {
    val solver = (DATAPATH / "2022/day14.txt").useLines { Solver(it) }
    solver.simulateFallingSand(Point(500, 0), true)
        .also { println("Part one: $it") }
    solver.simulateFallingSand(Point(500, 0), false)
        .also { println("Part two: $it") }
}
