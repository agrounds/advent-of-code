package com.groundsfam.advent.y2023.d23

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.pointOfFirst
import com.groundsfam.advent.grids.pointOfLast
import com.groundsfam.advent.grids.readGrid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.adjacents
import com.groundsfam.advent.points.go
import com.groundsfam.advent.points.s
import com.groundsfam.advent.timed
import com.groundsfam.advent.toDirection
import kotlin.io.path.div
import kotlin.math.max


private fun longestWalk(grid: Grid<Char>, slippery: Boolean): Int {
    val start = grid.pointOfFirst { it == '.' }
    val end = grid.pointOfLast { it == '.' }
    var longest = 0

    data class Walk(val position: Point, val visited: MutableSet<Point>)

    val partialWalks = ArrayDeque<Walk>()
    // to simplify things, start at the second position on the walk, which
    // is always the point south of start
    partialWalks.add(Walk(start.s, mutableSetOf(start)))

    while (partialWalks.isNotEmpty()) {
        val walk = partialWalks.removeFirst()
        var position: Point? = walk.position
        val visited = walk.visited

        while (position != null && position != end) {
            visited.add(position)
            var next: Point? = null

            position
                .adjacents(diagonal = false)
                .filter { p ->
                    when {
                        p in visited -> false
                        grid[p] == '#' -> false
                        slippery && grid[p].toDirection()?.let(p::go) == position -> false
                        else -> true
                    }
                }
                .forEach {
                    if (next == null) {
                        next = it
                    } else {
                        partialWalks.add(Walk(it, visited.toMutableSet()))
                    }
                }

            position = next
        }

        if (position == end) {
            longest = max(longest, visited.size)
        }
    }

    return longest
}

fun main() = timed {
    val grid = (DATAPATH / "2023/day23.txt")
        .readGrid()
    println("Part one: ${longestWalk(grid, slippery = true)}")
    println("Part two: ${longestWalk(grid, slippery = false)}")
}
