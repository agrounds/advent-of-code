package advent.y2015.d06

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

enum class Action {
    TurnOn,
    TurnOff,
    Toggle,
}
data class Instruction(val action: Action, val fromX: Int, val fromY: Int, val toX: Int, val toY: Int)

fun parseInstruction(line: String): Instruction {
    val parts = line.split(" ")
    var ret = Instruction(Action.TurnOn, 0, 0, 0, 0)
    when (parts[0]) {
        "turn" -> {
            ret = when (parts[1]) {
                "on" -> ret.copy(action = Action.TurnOn)
                "off" -> ret.copy(action = Action.TurnOff)
                else -> throw RuntimeException("Invalid instruction: $line")
            }
            parts[2].split(",").also {
                ret = ret.copy(fromX = it[0].toInt(), fromY = it[1].toInt())
            }
            parts[4].split(",").also {
                ret = ret.copy(toX = it[0].toInt(), toY = it[1].toInt())
            }
        }
        "toggle" -> {
            ret = ret.copy(action = Action.Toggle)
            parts[1].split(",").also {
                ret = ret.copy(fromX = it[0].toInt(), fromY = it[1].toInt())
            }
            parts[3].split(",").also {
                ret = ret.copy(toX = it[0].toInt(), toY = it[1].toInt())
            }
        }
        else -> throw RuntimeException("Invalid instruction: $line")
    }

    return ret
}

fun partOne(instructions: List<Instruction>): Int {
    val grid = Array(1000) { BooleanArray(1000) }
    instructions.forEach { ins ->
        (ins.fromX..ins.toX).forEach { x ->
            (ins.fromY..ins.toY).forEach { y ->
                when (ins.action) {
                    Action.TurnOn -> grid[x][y] = true
                    Action.TurnOff -> grid[x][y] = false
                    Action.Toggle -> grid[x][y] = !grid[x][y]
                }
            }
        }
    }
    return grid.sumOf { row -> row.count { it } }
}

fun partTwo(instructions: List<Instruction>): Int {
    val grid = Array(1000) { IntArray(1000) }
    instructions.forEach { ins ->
        (ins.fromX..ins.toX).forEach { x ->
            (ins.fromY..ins.toY).forEach { y ->
                when (ins.action) {
                    Action.TurnOn -> grid[x][y]++
                    Action.TurnOff -> grid[x][y] = maxOf(grid[x][y]-1, 0)
                    Action.Toggle -> grid[x][y] += 2
                }
            }
        }
    }
    return grid.sumOf { row -> row.sum() }
}


fun main() {
    val instructions = (DATAPATH / "2015/day06.txt").useLines { lines ->
        lines.toList().map(::parseInstruction)
    }

    partOne(instructions)
        .also { println("Part one: $it") }
    partTwo(instructions)
        .also { println("Part two: $it") }
}
