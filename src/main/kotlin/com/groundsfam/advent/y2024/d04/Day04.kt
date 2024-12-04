package com.groundsfam.advent.y2024.d04

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.forEachIndexed
import com.groundsfam.advent.grids.maybeGet
import com.groundsfam.advent.grids.readGrid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.adjacents
import com.groundsfam.advent.points.times
import com.groundsfam.advent.timed
import kotlin.io.path.div

val directions = Point(0, 0).adjacents(diagonal = true)
val diagonalDirections = listOf(
    listOf(Point(1, 1), Point(-1, -1)),
    listOf(Point(1, -1), Point(-1, 1)),
)

fun countXmas(grid: Grid<Char>): Int {
    var count = 0
    grid.forEachIndexed { p, c ->
        if (c == 'X') {
            directions.forEach { d ->
                val charsInLine = (1..3).map {
                    grid.maybeGet(p + it * d)
                }
                if (charsInLine == "MAS".toList()) {
                    count++
                }
            }
        }
    }
    return count
}

fun countXShapedMas(grid: Grid<Char>): Int {
    var count = 0
    grid.forEachIndexed { p, c ->
        if (c == 'A') {
            val diagonalChars = diagonalDirections.map { dirs ->
                dirs.mapTo(mutableSetOf()) { d ->
                    grid.maybeGet(p + d)
                }
            }
            if (diagonalChars == listOf("MS".toSet(), "MS".toSet())) {
                count++
            }
        }
    }
    return count
}

fun main() = timed {
    val grid = (DATAPATH / "2024/day04.txt").readGrid()
    println("Part one: ${countXmas(grid)}")
    println("Part two: ${countXShapedMas(grid)}")
}
