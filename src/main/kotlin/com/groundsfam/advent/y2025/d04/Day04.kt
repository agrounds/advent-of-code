package com.groundsfam.advent.y2025.d04

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.grids.*
import com.groundsfam.advent.points.adjacents
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.timed
import kotlin.io.path.div


fun removeRolls(grid: Grid<Char>): Pair<Int, Int> {
    val removals = mutableListOf<Int>()
    var prevRemovedRolls: Set<Point>? = null
    while (prevRemovedRolls?.isEmpty() != true) {
        val rollsToConsider =
            prevRemovedRolls
                ?.flatMapTo(mutableSetOf()) { it.adjacents() }
                ?: grid.pointIndices.toSet()
        val rollsToRemove = rollsToConsider
            .filterTo(mutableSetOf()) { p ->
                val adjacentRolls = p.adjacents().count {
                    grid.maybeGet(it) == '@'
                }
                grid.maybeGet(p) == '@' && adjacentRolls < 4
            }
        removals.add(rollsToRemove.size)
        rollsToRemove.forEach {
            grid[it] = '.'
        }
        prevRemovedRolls = rollsToRemove
    }
    return removals.first() to removals.sum()
}


fun main() = timed {
    val grid = (DATAPATH / "2025/day04.txt").readGrid()
    val (p1, p2) = removeRolls(grid)
    println("Part one: $p1")
    println("Part two: $p2")
}
