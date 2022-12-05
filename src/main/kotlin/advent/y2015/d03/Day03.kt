package advent.y2015.d03

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

fun partOne(directions: List<Direction>): Int {
    var position = 0 to 0
    val visited = mutableSetOf(position)
    directions.forEach { direction ->
        position = when (direction) {
            Direction.UP -> position.copy(first = position.first + 1)
            Direction.DOWN -> position.copy(first = position.first - 1)
            Direction.LEFT -> position.copy(second = position.second - 1)
            Direction.RIGHT -> position.copy(second = position.second + 1)
        }
        visited.add(position)
    }
    return visited.size
}

fun partTwo(directions: List<Direction>): Int {
    val positions = Array(2) { 0 to 0 }
    val visited = mutableSetOf(positions[0])
    directions.forEachIndexed { index, direction ->
        val i = index % 2
        positions[i] = positions[i].let {
            when (direction) {
                Direction.UP -> it.copy(first = it.first + 1)
                Direction.DOWN -> it.copy(first = it.first - 1)
                Direction.LEFT -> it.copy(second = it.second - 1)
                Direction.RIGHT -> it.copy(second = it.second + 1)
            }
        }
        visited.add(positions[i])
    }
    return visited.size
}


fun main() {
    val directions = (DATAPATH / "2015/day03.txt").useLines { lines ->
        lines.first().map { c ->
            when (c) {
                '^' -> Direction.DOWN
                'v' -> Direction.UP
                '<' -> Direction.LEFT
                '>' -> Direction.RIGHT
                else -> throw RuntimeException("Invalid character: $c")
            }
        }
    }

    println("Part one: ${partOne(directions)}")
    println("Part two: ${partTwo(directions)}")
}