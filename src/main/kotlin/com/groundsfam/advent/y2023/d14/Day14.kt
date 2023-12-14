package com.groundsfam.advent.y2023.d14

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.Direction.DOWN
import com.groundsfam.advent.Direction.LEFT
import com.groundsfam.advent.Direction.RIGHT
import com.groundsfam.advent.Direction.UP
import com.groundsfam.advent.Grid
import com.groundsfam.advent.copy
import com.groundsfam.advent.go
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.readGrid
import com.groundsfam.advent.timed
import kotlin.io.path.div


fun Grid<*>.directedIndices(direction: Direction): List<Point> = when (direction) {
    UP -> (0 until numRows).flatMap { y ->
        (0 until numCols).map { x ->
            Point(x, y)
        }
    }
    DOWN -> (0 until numRows).reversed().flatMap { y ->
        (0 until numCols).map { x ->
            Point(x, y)
        }
    }
    LEFT -> (0 until numCols).flatMap { x ->
        (0 until numRows).map { y ->
            Point(x, y)
        }
    }
    RIGHT -> (0 until numCols).reversed().flatMap { x ->
        (0 until numRows).map { y ->
            Point(x, y)
        }
    }
}

class Solution(platform: Grid<Char>) {
    val grid = platform.copy()

    fun tilt(direction: Direction) {
        grid.directedIndices(direction).forEach { p ->
            if (grid[p] == 'O') {
                // to will be the point that p slides north to
                var to = p
                var toNext = to.go(direction)
                while (grid.containsPoint(toNext) && grid[toNext] == '.') {
                    to = toNext
                    toNext = to.go(direction)
                }
                grid[to] = 'O'
                if (p != to) {
                    grid[p] = '.'
                }
            }
        }
    }

    fun load(): Int =
        grid.pointIndices.sumOf { p ->
            if (grid[p] == 'O') {
                grid.numRows - p.y
            } else {
                0
            }
        }
}

fun main() = timed {
    val solution = (DATAPATH / "2023/day14.txt")
        .readGrid()
        .let(::Solution)

    solution.tilt(UP)
    println("Part one: ${solution.load()}")
}
