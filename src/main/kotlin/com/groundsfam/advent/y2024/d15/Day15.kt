package com.groundsfam.advent.y2024.d15

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.grids.*
import com.groundsfam.advent.points.*
import com.groundsfam.advent.timed
import com.groundsfam.advent.toDirection
import kotlin.io.path.div
import kotlin.io.path.useLines

fun simulateRobot(grid: Grid<Char>, dirs: List<Direction>): Long {
    var p = grid.pointOfFirst { it == '@' }

    dirs.forEach { d ->
        val q = p.go(d)
        var box = q
        while (grid.maybeGet(box) == 'O') {
            box = box.go(d)
        }
        if (grid.maybeGet(box) == '.') {
            grid[box] = 'O'
            grid[q] = '.'
        }
        if (grid[q] == '.') {
            grid[q] = '@'
            grid[p] = '.'
            p = q
        }
    }

    return grid.pointIndices.sumOf { p1 ->
        if (grid[p1] == 'O') 100L * p1.y + p1.x
        else 0L
    }
}

fun simulateRobot2(grid: Grid<Char>, dirs: List<Direction>): Long {
    var p = grid.pointOfFirst { it == '@' }

    dirs.forEach { d ->
        val q = p.go(d)
        val boxesToMove = mutableListOf<Set<Point>>()
        var checkForBoxes = listOf(q)
        while (checkForBoxes.isNotEmpty()) {
            val foundBoxes = mutableSetOf<Point>()
            var hitWall = false
            checkForBoxes.forEach { r ->
                when (grid.maybeGet(r)) {
                    '.' -> { /* empty space, no additional points to check */ }
                    '[', ']' -> {
                        // if we're moving up or down, other half of the box
                        // has to also move up or down
                        // otherwise, we'll already be checking left/right movement anyway
                        // so no need to add the other half of the box to the set of found boxes
                        foundBoxes.add(r)
                        if (d in setOf(Direction.UP, Direction.DOWN)) {
                            foundBoxes.add(if (grid[r] == '[') r.e else r.w)
                        }
                    }
                    else -> {
                        hitWall = true
                    }
                }
            }
            if (hitWall) {
                boxesToMove.clear()
                checkForBoxes = emptyList()
            } else {
                // the last points we find are the first ones we want to move
                boxesToMove.add(0, foundBoxes)
                checkForBoxes = foundBoxes.map { it.go(d) }
            }
        }
        boxesToMove.forEach { points ->
            points.forEach { boxPoint ->
                grid[boxPoint.go(d)] = grid[boxPoint]
                // ensure first row of moved boxes are replaced with empty space
                grid[boxPoint] = '.'
            }
        }
        if (grid.maybeGet(q) == '.') {
            grid[q] = '@'
            grid[p] = '.'
            p = q
        }
    }

    return grid.pointIndices.sumOf { p1 ->
        if (grid[p1] == '[') 100L * p1.y + p1.x
        else 0L
    }
}

fun main() = timed {
    val (grid, dirs) = (DATAPATH / "2024/day15.txt").useLines { lines ->
        val iter = lines.iterator()
        val gridLines = mutableListOf<String>()
        val dirs = mutableListOf<Direction>()

        var line = iter.next()
        while (line.isNotBlank()) {
            gridLines.add(line)
            line = iter.next()
        }
        iter.forEach { l ->
            dirs.addAll(l.map { it.toDirection() ?: throw RuntimeException("Invalid direction $it") })
        }
        Pair(gridLines.parseGrid(), dirs)
    }
    val grid2 = Grid(grid.numRows, grid.numCols * 2) { (x, y) ->
        val p = Point(x / 2, y)
        when (grid[p]) {
            '#' -> '#'
            '.' -> '.'
            '@' -> if (x % 2 == 0) '@' else '.'
            'O' -> if (x % 2 == 0) '[' else ']'
            else -> throw RuntimeException("Invalid grid char ${grid[p]}")
        }
    }

    println("Part one: ${simulateRobot(grid, dirs)}")
    println("Part two: ${simulateRobot2(grid2, dirs)}")
}
