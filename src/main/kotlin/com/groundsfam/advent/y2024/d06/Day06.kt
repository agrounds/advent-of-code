package com.groundsfam.advent.y2024.d06

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.contains
import com.groundsfam.advent.grids.forEachIndexed
import com.groundsfam.advent.grids.maybeGet
import com.groundsfam.advent.grids.pointOfFirst
import com.groundsfam.advent.grids.readGrid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.go
import com.groundsfam.advent.timed
import kotlin.io.path.div

class Solution(private val grid: Grid<Char>, private val start: Point) {
    // map from x to set of ys such that grid[(x, y)] is an obstruction
    private val obstructionsXY = Array<MutableSet<Int>>(grid.numCols) { mutableSetOf() }
    // map from y to set of xs such that grid[(x, y)] is an obstruction
    private val obstructionsYX = Array<MutableSet<Int>>(grid.numRows) { mutableSetOf() }


    init {
        grid.forEachIndexed { (x, y), c ->
            if (c == '#') {
                obstructionsXY[x].add(y)
                obstructionsYX[y].add(x)
            }
        }
    }

    fun guardPath(): Set<Point> {
        // if d is in visited[p], then we have previously been at p going direction d
        val visited = mutableSetOf<Point>()
        var pos = start
        var dir = Direction.UP

        while (pos in grid) {
            visited.add(pos)

            while (grid.maybeGet(pos.go(dir)) == '#') {
                dir = dir.cw
            }
            pos = pos.go(dir)
        }

        return visited
    }

    private fun guardLoops(): Boolean {
        val visited = mutableMapOf<Point, MutableSet<Direction>>()
        var pos = start
        var dir = Direction.UP

        while (visited[pos]?.contains(dir) != true) {
            val dirs = visited[pos] ?: mutableSetOf()
            if (pos !in visited) {
                visited[pos] = dirs
            }
            dirs.add(dir)

            val (x, y) = pos
            pos = when (dir) {
                Direction.UP -> {
                    val nextY = obstructionsXY[x]
                        .filter { it < y }
                        .maxOrNull()
                        ?: return false
                    Point(x, nextY + 1)
                }
                Direction.DOWN -> {
                    val nextY = obstructionsXY[x]
                        .filter { it > y }
                        .minOrNull()
                        ?: return false
                    Point(x, nextY - 1)
                }
                Direction.LEFT -> {
                    val nextX = obstructionsYX[y]
                        .filter { it < x }
                        .maxOrNull()
                        ?: return false
                    Point(nextX + 1, y)
                }
                Direction.RIGHT -> {
                    val nextX = obstructionsYX[y]
                        .filter { it > x }
                        .minOrNull()
                        ?: return false
                    Point(nextX - 1, y)
                }
            }

            while (grid.maybeGet(pos.go(dir)) == '#') {
                dir = dir.cw
            }
        }

        return true
    }

    fun causesLoop(obstruction: Point): Boolean {
        if (obstruction == start || grid[obstruction] == '#') {
            return false
        }

        grid[obstruction] = '#'
        obstruction.let { (x, y) ->
            obstructionsXY[x].add(y)
            obstructionsYX[y].add(x)
        }

        val ret = guardLoops()

        grid[obstruction] = '.'
        obstruction.let { (x, y) ->
            obstructionsXY[x].remove(y)
            obstructionsYX[y].remove(x)
        }
        return ret
    }
}

fun main() = timed {
    val grid = (DATAPATH / "2024/day06.txt").readGrid()
    val start = grid.pointOfFirst { it == '^' }
    grid[start] = '.'
    val solution = Solution(grid, start)
    solution.guardPath()
        .also { println("Part one: ${it.size}") }
        .count(solution::causesLoop)
        .also { println("Part two: $it") }
}
