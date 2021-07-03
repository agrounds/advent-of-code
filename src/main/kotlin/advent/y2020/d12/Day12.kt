package advent.y2020.d12

import advent.y2020.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs

private enum class Direction {
    NORTH,
    SOUTH,
    EAST,
    WEST,
    LEFT,
    RIGHT,
    FORWARD;

    fun turnLeft(): Direction = when (this) {
        NORTH -> WEST
        WEST -> SOUTH
        SOUTH -> EAST
        EAST -> NORTH
        else -> this
    }

    fun turnRight(): Direction = when (this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
        else -> this
    }
}

private fun Char.toDirection(): Direction? =
    when (uppercase()) {
        "N" -> Direction.NORTH
        "S" -> Direction.SOUTH
        "E" -> Direction.EAST
        "W" -> Direction.WEST
        "L" -> Direction.LEFT
        "R" -> Direction.RIGHT
        "F" -> Direction.FORWARD
        else -> null
    }

private data class Instruction(val direction: Direction, val value: Int)
private fun parseInstruction(line: String): Instruction {
    if (line.length < 2) throw RuntimeException("Cannot parse instruction from \"$line\"")
    val direction = line[0].toDirection() ?: throw RuntimeException("Unknown direction: ${line[0]}")
    val value = line.substring(1).toInt()
    return Instruction(direction, value)
}

private fun applyInstructions1(instructions: List<Instruction>, initialDirection: Direction = Direction.EAST): Pair<Int, Int> {
    var x = 0
    var y = 0
    var direction = initialDirection

    instructions.forEach { (dir, value) ->
        when (dir) {
            Direction.NORTH -> y += value
            Direction.SOUTH -> y -= value
            Direction.EAST -> x -= value
            Direction.WEST -> x += value
            Direction.LEFT -> repeat(value / 90) { direction = direction.turnLeft() }
            Direction.RIGHT -> repeat(value / 90) { direction = direction.turnRight() }
            Direction.FORWARD -> when (direction) {
                Direction.NORTH -> y += value
                Direction.SOUTH -> y -= value
                Direction.EAST -> x -= value
                Direction.WEST -> x += value
                else -> {} // do nothing
            }
        }
    }

    return x to y
}

private fun applyInstructions2(instructions: List<Instruction>,
                               initialWaypoint: Pair<Int, Int> = -10 to 1): Pair<Int, Int> {
    var x = 0
    var y = 0
    var (wayX, wayY) = initialWaypoint

    instructions.forEach { (dir, value) ->
        when (dir) {
            Direction.NORTH -> wayY += value
            Direction.SOUTH -> wayY -= value
            Direction.EAST -> wayX -= value
            Direction.WEST -> wayX += value
            Direction.LEFT -> repeat(value / 90) {
                val tmp = wayX
                wayX = wayY
                wayY = -tmp
            }
            Direction.RIGHT -> repeat(value / 90) {
                val tmp = wayX
                wayX = -wayY
                wayY = tmp
            }
            Direction.FORWARD -> {
                x += value * wayX
                y += value * wayY
            }
        }
    }

    return x to y
}

fun main() {
    val instructions = (DATAPATH / "day12.txt").useLines { it.map(::parseInstruction).toList() }

    applyInstructions1(instructions)
        .also { (x, y) -> println("Part one: ${abs(x) + abs(y)}") }
    applyInstructions2(instructions)
        .also { (x, y) -> println("Part two: ${abs(x) + abs(y)}") }
}