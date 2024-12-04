package com.groundsfam.advent.y2015.d18

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.count
import com.groundsfam.advent.grids.maybeGet
import com.groundsfam.advent.grids.readGrid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.adjacents
import com.groundsfam.advent.timed
import kotlin.io.path.div

const val STEPS = 100

fun step(grid: Grid<Boolean>, stuckCorners: Boolean): Grid<Boolean> {
    val corners = setOf(
        Point(0, 0),
        Point(0, grid.numRows - 1),
        Point(grid.numCols - 1, 0),
        Point(grid.numCols - 1, grid.numRows - 1)
    )
    val nextGrid = Grid(grid.numRows, grid.numCols) { p ->
        if (stuckCorners && p in corners) {
            true
        } else {
            val isLit = grid[p]
            val litNeighbors = p.adjacents(true)
                .count { grid.maybeGet(it) == true }
            litNeighbors == 3 || (isLit && litNeighbors == 2)
        }
    }
    return nextGrid
}

fun runGrid(grid: Grid<Boolean>, stuckCorners: Boolean): Int =
    (1..STEPS)
        .fold(grid) { prevGrid, _ ->
            step(prevGrid, stuckCorners)
        }
        .count { it }

fun main() = timed {
    val grid = (DATAPATH / "2015/day18.txt")
        .readGrid { it == '#' }
    println("Part one: ${runGrid(grid, false)}")
    println("Part two: ${runGrid(grid, true)}")
}
