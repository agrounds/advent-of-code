package com.groundsfam.advent.y2021.d11

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Grid
import com.groundsfam.advent.copy
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.adjacents
import com.groundsfam.advent.readGrid
import com.groundsfam.advent.timed
import kotlin.io.path.div


class Solution(octopuses: Grid<Int>) {
    private val grid = octopuses.copy()
    private var steps = 0
    private var allFlashStep: Int? = null

    fun step(): Int {
        var flashes = 0
        val didFlash = mutableSetOf<Point>()
        val willFlash = ArrayDeque<Point>()

        // increment energy level of this octopus
        // and add it to willFlash queue if needed
        fun increaseEnergy(p: Point) {
            grid[p] += 1
            if (grid[p] > 9 && p !in didFlash) {
                willFlash.add(p)
            }
        }

        // increase energy of all octopuses
        grid.pointIndices
            .forEach(::increaseEnergy)

        while (willFlash.isNotEmpty()) {
            val p = willFlash.removeFirst()
            if (p !in didFlash) {
                // perform flash
                flashes++
                p.adjacents()
                    .filter(grid::containsPoint)
                    .forEach(::increaseEnergy)
                didFlash.add(p)
            }
        }

        // reset energy of every octopus that flashed to zero
        didFlash.forEach { p ->
            grid[p] = 0
        }

        steps++
        if (allFlashStep == null && flashes == grid.gridSize) {
            allFlashStep = steps
        }
        return flashes
    }

    fun stepUntilAllFlash(): Int {
        while (allFlashStep == null) {
            step()
        }
        return allFlashStep!!
    }
}

fun main() = timed {
    val solution = (DATAPATH / "2021/day11.txt")
        .readGrid(Char::digitToInt)
        .let(::Solution)

    val partOne = (1..100).sumOf {
        solution.step()
    }
    println("Part one: $partOne")
    println("Part two ${solution.stepUntilAllFlash()}")
}
