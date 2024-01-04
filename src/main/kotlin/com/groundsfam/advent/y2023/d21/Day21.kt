package com.groundsfam.advent.y2023.d21

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.maybeGet
import com.groundsfam.advent.grids.pointOfFirst
import com.groundsfam.advent.grids.readGrid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.adjacents
import com.groundsfam.advent.timed
import kotlin.io.path.div


private fun reachablePlots(grid: Grid<Char>, numSteps: Int): Int {
    val start = grid.pointOfFirst { it == 'S' }
    val visited = mutableSetOf(start)
    val reachable = mutableSetOf(start)

    (1..numSteps).fold(setOf(start)) { prevPoints, i ->
        val nextPoints = mutableSetOf<Point>()

        prevPoints.forEach { p ->
            p.adjacents(diagonal = false).forEach { q ->
                if (grid.maybeGet(q) == '.' && q !in visited) {
                    visited.add(q)
                    nextPoints.add(q)
                    if ((numSteps - i) % 2 == 0) {
                        reachable.add(q)
                    }
                }
            }
        }

        nextPoints
    }

    return reachable.size
}

fun main() = timed {
    val grid = (DATAPATH / "2023/day21.txt")
        .readGrid()
    println("Part one: ${reachablePlots(grid, 64)}")
}
