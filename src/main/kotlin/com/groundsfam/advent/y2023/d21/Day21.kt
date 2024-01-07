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


private class Solution(private val grid: Grid<Char>) {
    private val start: Point = grid.pointOfFirst { it == 'S' }
    private val reachablePlotCache = mutableMapOf<Point, List<Int>>()

    /**
     * Give a starting point, find the number of plots that can be reached in
     * 0, 1, 2, 3, ... steps. Reachable plots are limited to a single grid
     * within the infinite plane, and all reachable plots within the grid are
     * eagerly found.
     *
     * The returned list is effectively a map from number of steps allowed to
     * number of plots that are reachable in that number of steps. For example,
     * `stepsToPlotsInGrid(from)[5]` is equal to the number of plots in one
     * grid that are reachable in exactly 5 steps, starting at the point `from`.
     *
     * @param from the starting point
     */
    private fun stepsToPlotsInGrid(from: Point): List<Int> {
        reachablePlotCache[from]?.also { return it }

        val visited = mutableSetOf(from)
        val reachable = mutableListOf(1)
        var prevPoints = setOf(from)

        while (prevPoints.isNotEmpty()) {
            val nextPoints = mutableSetOf<Point>()

            prevPoints.forEach { p ->
                p.adjacents(diagonal = false).forEach { q ->
                    if (grid.maybeGet(q) in setOf('.', 'S') && q !in visited) {
                        visited.add(q)
                        nextPoints.add(q)
                    }
                }
            }
            val prevVisitedReachable = if (reachable.size >= 2) reachable[reachable.size - 2] else 0
            reachable.add(prevVisitedReachable + nextPoints.size)
            prevPoints = nextPoints
        }

        return reachable.also {
            reachablePlotCache[from] = it
        }
    }

    /**
     * The number of steps required to reach the furthest reachable plot in a single grid,
     * starting at the point [from].
     *
     * @param from the starting point
     */
    private fun maxSteps(from: Point): Int =
        stepsToPlotsInGrid(from).size - 1

    /**
     * The number of plots reachable within a single grid for paths starting at [from] and
     * taking exactly [numSteps] steps.
     *
     * This function is a helpful layer on top of [stepsToPlotsInGrid]. It accounts for cases
     * in which [numSteps] exceeds the number of steps required to reach the furthest
     * reachable plot within a single grid.
     *
     * @param from the starting point
     * @param numSteps the number of steps to take in each path
     */
    private fun reachablePlotsInGrid(from: Point, numSteps: Int): Int {
        val maxSteps = maxSteps(from)
        val steps =
            if (numSteps >= maxSteps) maxSteps - ((numSteps - maxSteps) % 2)
            else numSteps
        return stepsToPlotsInGrid(from)[steps]
    }

    private fun findReachablePlots(from: Point, numSteps: Int): Long {
        if (numSteps < 0) {
            return 0
        }

        val sideLen = grid.numCols

        // detect if we're computing plots for this grid only,
        // or along just an axis, or for a quadrant (via diagonal paths)
        if (from == start) {
            return reachablePlotsInGrid(from, numSteps).toLong()
        }
        val diagonalPaths = from.x != start.x && from.y != start.y

        // number of grids we can reach with enough steps remaining to reach all plots therein
        // call these "fully-explorable" grids
        // this is measured only along a straight line, not the full quadrant of grids we can reach
        // by diagonal paths
        val numGrids =
            if (numSteps >= maxSteps(from)) (numSteps - maxSteps(from)) / sideLen
            else 0
        // number of fully-explorable grids that will have same parity of stepsLeft as starting grid
        val numGrids1 = ((numGrids + 1) / 2)
            .toLong()
            .let {
                // use the sum of odd numbers formula
                // 1 + 3 + 5 + ... + 2(n+1) = n^2
                if (diagonalPaths) it * it
                else it
            }
        // number of full-explorable grids that will have opposite parity of stepsLeft as starting grid
        val numGrids2 = (numGrids / 2)
            .toLong()
            .let {
                // use the sum of even numbers formula
                // 2 + 4 + 6 + ... + 2n = n(n+1)
                if (diagonalPaths) it * (it + 1)
                else it
            }

        var sum =
            reachablePlotsInGrid(from, numSteps) * numGrids1 +
                reachablePlotsInGrid(from, numSteps - 1) * numGrids2

        // find reachable plots in the non-fully-explorable grids
        var gridNum = numGrids + 1
        var remainingSteps = numSteps - numGrids * sideLen
        while (remainingSteps >= 0) {
            // if using diagonal paths, multiply by gridNum because there is a whole diagonal line
            // of grids in which we'll have this many steps remaining
            sum += reachablePlotsInGrid(from, remainingSteps) *
                (if (diagonalPaths) gridNum else 1)
            gridNum++
            remainingSteps -= sideLen
        }

        return sum
    }

    fun findReachablePlots(numSteps: Int): Long {
        val sideLen = grid.numCols
        // use assumption that start is in the exact middle of the odd-length square grid
        val stepsToEdge = (sideLen + 1) / 2
        return listOf(
            // start grid
            Pair(start, 0),
            // axes
            Pair(start.copy(x = 0), 1),
            Pair(start.copy(y = 0), 1),
            Pair(start.copy(x = sideLen - 1), 1),
            Pair(start.copy(y = sideLen - 1), 1),
            // quadrants
            Pair(Point(0, 0), 2),
            Pair(Point(0, sideLen - 1), 2),
            Pair(Point(sideLen - 1, 0), 2),
            Pair(Point(sideLen - 1, sideLen - 1), 2),
        ).sumOf { (from, i) ->
            findReachablePlots(from, numSteps - i * stepsToEdge)
        }
    }
}

fun main() = timed {
    val solution = (DATAPATH / "2023/day21.txt")
        .readGrid()
        .let(::Solution)
    println("Part one: ${solution.findReachablePlots(64)}")
    println("Part two: ${solution.findReachablePlots(26_501_365)}")
}
