package com.groundsfam.advent.y2021.d15

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Grid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.e
import com.groundsfam.advent.points.n
import com.groundsfam.advent.points.s
import com.groundsfam.advent.points.w
import com.groundsfam.advent.readGrid
import com.groundsfam.advent.timed
import org.apache.commons.lang3.math.NumberUtils.min
import kotlin.io.path.div


private fun leastRiskPath(grid: Grid<Int>): Int {
    val start = Point(0, 0)
    val end = Point(grid.numCols - 1, grid.numRows - 1)
    var unvisitedPoints = grid.pointIndices.toMutableSet()
    val distances = grid.mapIndexed { p, _ ->
        if (p == start) 0
        else Int.MAX_VALUE
    }
    var curr = start

    while (curr != end) {
        val d = distances[curr]
        unvisitedPoints.remove(curr)

        listOf(curr.n, curr.s, curr.e, curr.w)
            .filter(grid::containsPoint)
            .filter(unvisitedPoints::contains)
            .forEach { neighbor ->
//                distances[neighbor] = min(distances[neighbor], d + )
            }
    }

    return distances[end]
}

fun main() = timed {
    val grid = (DATAPATH / "2021/day15-example.txt")
        .readGrid(Char::digitToInt)
}
