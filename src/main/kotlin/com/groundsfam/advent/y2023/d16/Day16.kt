package com.groundsfam.advent.y2023.d16

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.Direction.DOWN
import com.groundsfam.advent.Direction.LEFT
import com.groundsfam.advent.Direction.RIGHT
import com.groundsfam.advent.Direction.UP
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.containsPoint
import com.groundsfam.advent.grids.count
import com.groundsfam.advent.grids.readGrid
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.go
import com.groundsfam.advent.timed
import kotlin.io.path.div


private data class LightBeam(val position: Point, val direction: Direction)

private fun nextDirections(tile: Char, direction: Direction): List<Direction> =
    when (tile) {
        '.' -> listOf(direction)
        '/' ->
            when (direction) {
                UP -> listOf(RIGHT)
                RIGHT -> listOf(UP)
                DOWN -> listOf(LEFT)
                LEFT -> listOf(DOWN)
            }
        '\\' ->
            when (direction) {
                UP -> listOf(LEFT)
                LEFT -> listOf(UP)
                DOWN -> listOf(RIGHT)
                RIGHT -> listOf(DOWN)
            }
        '|' ->
            if (direction.isHorizontal()) listOf(UP, DOWN)
            else listOf(direction)
        '-' ->
            if (direction.isVertical()) listOf(LEFT, RIGHT)
            else listOf(direction)
        else -> throw RuntimeException("Invalid tile $tile")
    }

private fun findEnergizedTiles(grid: Grid<Char>, start: LightBeam): Int {
    val energized: Grid<MutableSet<Direction>> = Grid(grid.numRows, grid.numCols) { mutableSetOf() }

    val queue = ArrayDeque<LightBeam>()
    queue.add(start)

    while (queue.isNotEmpty()) {
        var (position, direction) = queue.removeFirst()
        while (grid.containsPoint(position) && energized[position].add(direction)) {
            val nextDirs = nextDirections(grid[position], direction)
            if (nextDirs.size == 1) {
                val dir = nextDirs[0]
                position = position.go(dir)
                direction = dir
            } else {
                // must be a splitter with two directions
                // add opposite direction to same tile because result
                // will be the same for either direction
                energized[position].add(-direction)

                val (dir1, dir2) = nextDirs
                position = position.go(dir1)
                direction = dir1
                queue.add(LightBeam(position.go(dir2), dir2))
            }
        }
    }

    return energized.count { it.isNotEmpty() }
}

private fun maximizeEnergizedTiles(grid: Grid<Char>): Int =
    listOf(
        (0 until grid.numCols).maxOf { x ->
            findEnergizedTiles(grid, LightBeam(Point(x, 0), DOWN))
        },
        (0 until grid.numCols).maxOf { x ->
            findEnergizedTiles(grid, LightBeam(Point(x, grid.numRows - 1), UP))
        },
        (0 until grid.numRows).maxOf { y ->
            findEnergizedTiles(grid, LightBeam(Point(0, y), RIGHT))
        },
        (0 until grid.numRows).maxOf { y ->
            findEnergizedTiles(grid, LightBeam(Point(grid.numCols - 1, y), LEFT))
        },
    ).max()

fun main() = timed {
    val grid = (DATAPATH / "2023/day16.txt")
        .readGrid()
    println("Part one: ${findEnergizedTiles(grid, LightBeam(Point(0, 0), RIGHT))}")
    println("Part two: ${maximizeEnergizedTiles(grid)}")
}
