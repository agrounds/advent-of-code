package com.groundsfam.advent.y2024.d10

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.maybeGet
import com.groundsfam.advent.grids.readGrid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.adjacents
import com.groundsfam.advent.timed
import kotlin.io.path.div

fun getTrailheadScores(grid: Grid<Int>): Pair<Int, Int> {
    val reachablePeaks = Grid<Set<Point>?>(grid.numRows, grid.numCols) { null }
    val numTrails = Grid<Int?>(grid.numRows, grid.numCols) { null }

    fun populateGrids(p: Point) {
        if (reachablePeaks[p] != null) return
        val h = grid[p]
        if (h == 9) {
            reachablePeaks[p] = setOf(p)
            numTrails[p] = 1
        } else {
            val peaks = mutableSetOf<Point>()
            var trails = 0
            p.adjacents(diagonal = false)
                .forEach { q ->
                    if (grid.maybeGet(q) == h + 1) {
                        populateGrids(q)
                        peaks.addAll(reachablePeaks[q]!!)
                        trails += numTrails[q]!!
                    }
                }
            reachablePeaks[p] = peaks
            numTrails[p] = trails
        }
    }



    return grid.pointIndices.fold(0 to 0) { (peaks, trails), p ->
        if (grid[p] == 0) {
            populateGrids(p)
            (peaks + reachablePeaks[p]!!.size) to (trails + numTrails[p]!!)
        } else {
            peaks to trails
        }
    }
}

fun main() = timed {
    val grid = (DATAPATH / "2024/day10.txt").readGrid(Char::digitToInt)
    val (peaks, trails) = getTrailheadScores(grid)
    println("Part one: $peaks")
    println("Part two: $trails")
}
