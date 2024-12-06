package com.groundsfam.advent.y2024.d06

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.contains
import com.groundsfam.advent.grids.maybeGet
import com.groundsfam.advent.grids.pointOfFirst
import com.groundsfam.advent.grids.readGrid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.go
import com.groundsfam.advent.timed
import kotlin.io.path.div

class Solution(private val grid: Grid<Char>, private val start: Point) {
    // returns (num visited locations, whether last position was in grid)
    fun guardPath(): Pair<Int, Boolean> {
        // if d is in visited[p], then we have previously been at p going direction d
        val visited = mutableMapOf<Point, MutableSet<Direction>>()
        var pos = start
        var dir = Direction.UP

        while (pos in grid && visited[pos]?.contains(dir) != true) {
            val dirs = visited[pos] ?: mutableSetOf()
            if (pos !in visited) {
                visited[pos] = dirs
            }
            dirs.add(dir)

            while (grid.maybeGet(pos.go(dir)) == '#') {
                dir = dir.cw
            }
            pos = pos.go(dir)
        }

        return visited.size to (pos in grid)
    }

    fun causesLoop(obstruction: Point): Boolean {
        if (obstruction == start || grid[obstruction] == '#') {
            return false
        }
        grid[obstruction] = '#'

        val path = guardPath()

        grid[obstruction] = '.'
        return path.second
    }
}

fun main() = timed {
    val grid = (DATAPATH / "2024/day06.txt").readGrid()
    val start = grid.pointOfFirst { it == '^' }
    grid[start] = '.'
    val solution = Solution(grid, start)
    println("Part one: ${solution.guardPath().first}")
    grid.pointIndices
        .count(solution::causesLoop)
        .also { println("Part two: $it") }
}
