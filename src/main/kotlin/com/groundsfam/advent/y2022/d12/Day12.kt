package com.groundsfam.advent.y2022.d12

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Grid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.down
import com.groundsfam.advent.points.left
import com.groundsfam.advent.points.right
import com.groundsfam.advent.points.up
import com.groundsfam.advent.timed
import com.groundsfam.advent.toGrid
import kotlin.io.path.div
import kotlin.io.path.useLines


fun Grid<Int>.height(p: Point): Int? =
    if (p in this.pointIndices) this[p]
    else null

// Find min distance path from `start` to any given target point via Dikjstra's algorithm
class PartOneSolver(private val grid: Grid<Int>, start: Point) {
    private val toVisit = mutableSetOf(start)
    private val visited = mutableSetOf<Point>()
    private val distances = Array(grid.numRows) { IntArray(grid.numCols) { Integer.MAX_VALUE } }
        .apply { this[start.y][start.x] = 0 }

    private fun neighbors(point: Point): List<Point> =
        listOf(point.left, point.right, point.up, point.down)
            .filter { neighbor ->
                grid.height(neighbor).let {
                    it != null && it <= grid.height(point)!! + 1
                }
            }

    private fun currDistance(point: Point): Int = distances[point.y][point.x]

    fun minDistance(to: Point): Int {
        while (to !in visited) {
            val next = toVisit.minByOrNull(::currDistance)
                ?: throw RuntimeException("Set of points to visit is empty even though graph is not fully explored!")

            toVisit.remove(next)
            neighbors(next)
                .filterNot { it in visited }
                .forEach { neighbor ->
                    distances[neighbor.y][neighbor.x] = minOf(
                        currDistance(neighbor),
                        currDistance(next) + 1
                    )
                    toVisit.add(neighbor)
                }
            visited.add(next)
        }
        return currDistance(to)
    }
}

// Find shortest path from any minimum-height starting point to the `end` point via Dijstra's algorithm.
// This solver works backwards, allowing points to be neighbors if the elevation goes up, or goes down by at most 1.
class PartTwoSolver(private val grid: Grid<Int>, end: Point) {
    private val toVisit = mutableSetOf(end)
    private val visited = mutableSetOf<Point>()
    private val distances = Array(grid.numRows) { IntArray(grid.numCols) { Integer.MAX_VALUE } }
        .apply { this[end.y][end.x] = 0 }

    private fun neighbors(point: Point): List<Point> =
        listOf(point.left, point.right, point.up, point.down)
            .filter { neighbor ->
                grid.height(neighbor).let {
                    it != null && it >= grid.height(point)!! - 1
                }
            }

    private fun currDistance(point: Point): Int = distances[point.y][point.x]

    fun minPath(startingHeight: Int): Int? {
        while (toVisit.isNotEmpty()) {
            val next = toVisit.minByOrNull(::currDistance)!!
            if (grid.height(next) == startingHeight)
                return currDistance(next)

            toVisit.remove(next)
            neighbors(next)
                .filterNot { it in visited }
                .forEach { neighbor ->
                    distances[neighbor.y][neighbor.x] = minOf(
                        currDistance(neighbor),
                        currDistance(next) + 1
                    )
                    toVisit.add(neighbor)
                }
            visited.add(next)
        }
        return null
    }
}


fun main() = timed {
    var start = Point(0, 0)
    var end = Point(0, 0)

    val grid: Grid<Int> = (DATAPATH / "2022/day12.txt").useLines { lines ->
        lines.mapIndexedTo(mutableListOf()) { y, line ->
            line.mapIndexed { x, c ->
                when (c) {
                    'S' -> {
                        start = Point(x, y)
                        0
                    }
                    'E' -> {
                        end = Point(x, y)
                        25
                    }
                    else ->
                        c - 'a'
                }
            }
        }
            .toGrid()
    }
    PartOneSolver(grid, start).minDistance(end)
        .also { println("Part one: $it") }
    PartTwoSolver(grid, end).minPath(0)
        .also { println("Part two: $it") }
}
