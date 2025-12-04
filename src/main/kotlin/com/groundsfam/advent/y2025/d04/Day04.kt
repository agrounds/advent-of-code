package com.groundsfam.advent.y2025.d04

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.grids.*
import com.groundsfam.advent.points.adjacents
import com.groundsfam.advent.timed
import kotlin.io.path.div


fun removeRolls(grid: Grid<Char>): Pair<Int, Int> {
    val removals = mutableListOf<Int>()
    do {
        val rollsToRemove = grid.mapIndexedNotNullTo(mutableSetOf()) { p, c ->
            val adjacentRolls = p.adjacents(diagonal = true).count {
                grid.maybeGet(it) == '@'
            }
            p.takeIf {
                c == '@' && adjacentRolls < 4
            }
        }
        removals.add(rollsToRemove.size)
        rollsToRemove.forEach {
            grid[it] = '.'
        }
    } while (removals.last() > 0)
    return removals.first() to removals.sum()
}


fun main() = timed {
    val grid = (DATAPATH / "2025/day04.txt").readGrid()
    val (p1, p2) = removeRolls(grid)
    println("Part one: $p1")
    println("Part two: $p2")
}
