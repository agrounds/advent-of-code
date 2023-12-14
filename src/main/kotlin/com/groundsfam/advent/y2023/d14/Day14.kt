package com.groundsfam.advent.y2023.d14

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Grid
import com.groundsfam.advent.copy
import com.groundsfam.advent.points.n
import com.groundsfam.advent.readGrid
import com.groundsfam.advent.timed
import kotlin.io.path.div


fun tiltNorth(platform: Grid<Char>): Int {
    val grid = platform.copy()
    var load = 0

    platform.pointIndices.forEach { p ->
        if (grid[p] == 'O') {
            // to will be the point that p slides north to
            var to = p
            while (grid.containsPoint(to.n) && grid[to.n] == '.') {
                to = to.n
            }
            grid[to] = 'O'
            if (p != to) {
                grid[p] = '.'
            }
            load += platform.numRows - to.y
        }
    }

    return load
}

fun main() = timed {
    val platform = (DATAPATH / "2023/day14.txt")
        .readGrid()

    println("Part one: ${tiltNorth(platform)}")
}
