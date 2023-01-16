package advent.y2022.d24

import advent.DATAPATH
import advent.Direction
import advent.Point
import advent.asPoint
import advent.toDirection
import kotlin.io.path.div
import kotlin.io.path.useLines

class Solver(grid: List<String>) {
    private val width = grid[0].length
    private val height = grid.size
    var time = 0
        private set

    private var blizzards = mutableMapOf<Point, List<Direction>>().apply {
        grid.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                c.toDirection()?.also { dir ->
                    this[Point(x, y)] = (this[Point(x, y)] ?: emptyList()) + listOf(dir)
                }
            }
        }
    }

    private fun timeIncrement() {
        blizzards = mutableMapOf<Point, List<Direction>>().apply {
            blizzards.forEach { (position, dirs) ->
                dirs.forEach { dir ->
                    val nextPosition = (position + dir.asPoint())
                        .let { (x, y) ->
                            Point(x.mod(width), y.mod(height))  // new blizzard forms by opposite wall
                        }
                    this[nextPosition] = (this[nextPosition] ?: emptyList()) + listOf(dir)
                }
            }
        }
        time++
    }

    // reversed = true indicates we're going from the end to the start
    fun goToGoal(reversed: Boolean) {
        val start = if (reversed) Point(width - 1, height) else Point(0, -1)
        val goal = if (reversed) Point(0, 0) else Point(width - 1, height - 1)
        var possiblePositions = setOf(start)
        // (width - 1, height - 1) is one move away from the goal
        while (goal !in possiblePositions) {
            timeIncrement()

            possiblePositions = mutableSetOf<Point>().apply {
                possiblePositions.forEach { position ->
                    // one option is to wait where we are
                    if (position !in blizzards) {
                        add(position)
                    }
                    // other options are to move up, down, left or right
                    Direction.values().forEach { dir ->
                        val nextPosition = position + dir.asPoint()
                        if (
                            nextPosition.x in 0 until width
                            && nextPosition.y in 0 until height
                            && nextPosition !in blizzards
                        ) {
                            add(nextPosition)
                        }
                    }
                }
            }
        }
        // simulate final move to the goal
        timeIncrement()
    }
}


fun main() {
    val solver = (DATAPATH / "2022/day24.txt").useLines { it.toList() }
        .let { it.subList(1, it.size - 1) }  // remove first and last lines
        .map { line ->
            line.substring(1..line.length - 2)  // remove first and last chars of each line
        }
        .let(::Solver)
    solver.goToGoal(false)
    println("Part one: ${solver.time}")
    solver.goToGoal(true)
    solver.goToGoal(false)
    println("Part two: ${solver.time}")
}
