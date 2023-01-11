package advent.y2022.d22

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

sealed class Instruction
data class Move(val amount: Int) : Instruction()
data class Turn(val left: Boolean) : Instruction()
fun parseInstructions(directions: String): List<Instruction> {
    var i = 0
    val ret = mutableListOf<Instruction>()

    while (i < directions.length) {
        when {
            directions[i] == 'L' -> {
                ret.add(Turn(true))
                i++
            }
            directions[i] == 'R' -> {
                ret.add(Turn(false))
                i++
            }
            else -> {
                directions.substring(i)
                    .takeWhile { it in '0'..'9' }
                    .also { i += it.length }
                    .toInt()
                    .also { ret.add(Move(it)) }
            }
        }
    }

    return ret
}

fun followInstructions1(map: List<String>, instructions: List<Instruction>): Int {
    var x = map.first().indexOfFirst { it == '.' }
    var y = 0
    // 0 = right, 1 = down, 2 = left, 3 = up
    var facing = 0

    instructions.forEach { instruction ->
        when (instruction) {
            is Turn -> {
                facing =
                    if (instruction.left) (facing - 1).mod(4)
                    else (facing + 1).mod(4)
            }
            is Move -> {
                if (facing % 2 == 0) {  // moving right or left
                    val dx = 1 - facing
                    var i = 0
                    while (i < instruction.amount) {
                        var nextX = (x + dx).mod(map[y].length)
                        while (map[y][nextX] == ' ') {
                            nextX = (nextX + dx).mod(map[y].length)
                        }
                        if (map[y][nextX] == '#') {
                            break
                        }
                        x = nextX
                        i++
                    }
                } else {  // moving up or down
                    val dy = 2 - facing
                    var i = 0
                    while (i < instruction.amount) {
                        var nextY = (y + dy).mod(map.size)
                        while (x >= map[nextY].length || map[nextY][x] == ' ') {
                            nextY = (nextY + dy).mod(map.size)
                        }
                        if (map[nextY][x] == '#') {
                            break
                        }
                        y = nextY
                        i++
                    }
                }
            }
        }
    }

    return 1000 * (y + 1) + 4 * (x + 1) + facing
}

fun followInstructions2(map: List<String>, instructions: List<Instruction>): Int {
    val cube = parseCube(map)
    return 0
}


fun main() {
    val (map, instructions) = (DATAPATH / "2022/day22-example.txt").useLines { it.toList() }.let { lines ->
        lines.take(lines.size - 2) to parseInstructions(lines.last())
    }
    println("Part one: ${followInstructions1(map, instructions)}")
    println("Part two: ${followInstructions2(map, instructions)}")
}
