package advent.y2022.d22

import advent.DATAPATH
import advent.Point
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
    val sideLen = map.sideLen
    val cube = parseCube(map)
    // x, y, and facing are still values as appear on the original map
    var x = map.first().indexOfFirst { it == '.' }
    var y = 0
    // 0 = right, 1 = down, 2 = left, 3 = up
    var facing = 0
    // cubeFace tracks which face of the cube we're currently on
    var cubeFace = cube.indexOfFirst { it.upperLeft == Point(x, y) }

    instructions.forEach { instruction ->
        when (instruction) {
            is Turn -> {
                facing =
                    if (instruction.left) (facing - 1).mod(4)
                    else (facing + 1).mod(4)
            }
            is Move -> {
                var i = 0
                while (i < instruction.amount) {
                    var (nextX, nextY) = when (facing) {
                        0 -> Point(x + 1, y)
                        1 -> Point(x, y + 1)
                        2 -> Point(x - 1, y)
                        3 -> Point(x, y - 1)
                        else -> throw RuntimeException("Illegal facing $facing")
                    }
                    var nextFacing = facing
                    var nextCubeFace = cubeFace

                    // check if nextX, nextY is crossing into a new face. if so, recompute nextX, nextY
                    // using `cube`. then either break loop (if nextPoint is a wall),
                    // or update x, y, facing, and cubeFace appropriately

                    val edgeAndDist = when {
                        x.floorDiv(sideLen) < nextX.floorDiv(sideLen) -> { // crossed right edge
                            0 to y.mod(sideLen)
                        }
                        y.floorDiv(sideLen) < nextY.floorDiv(sideLen) -> { // crossed bottom edge
                            1 to (sideLen - 1 - x).mod(sideLen)
                        }
                        x.floorDiv(sideLen) > nextX.floorDiv(sideLen) -> { // crossed left boundary
                            2 to (sideLen - 1 - y).mod(sideLen)
                        }
                        y.floorDiv(sideLen) > nextY.floorDiv(sideLen) -> { // crossed top edge
                            3 to x.mod(sideLen)

                        }
                        else -> null
                    }
                    // edgeDist is the distance in the clockwise direction from the edge's corner to the point (x, y)
                    edgeAndDist?.let { (edgeNum, edgeDist) ->
                        val edge = cube[cubeFace].edges[edgeNum]
                        (cube[edge.adjFace].upperLeft + when (edge.adjFaceEdge) {
                            0 -> Point(sideLen - 1, sideLen - 1 - edgeDist)
                            1 -> Point(edgeDist, sideLen - 1)
                            2 -> Point(0, edgeDist)
                            3 -> Point(sideLen - 1 - edgeDist, 0)
                            else -> throw RuntimeException("Illegal edge ${edge.adjFaceEdge}")
                        }).also {
                            nextX = it.x
                            nextY = it.y
                        }
                        nextFacing = (facing + edge.adjFaceEdge - edgeNum + 2).mod(4)
                        nextCubeFace = edge.adjFace
                    }

                    if (map[nextY][nextX] == '#') break

                    x = nextX
                    y = nextY
                    facing = nextFacing
                    cubeFace = nextCubeFace

                    i++
                }
            }
        }
    }

    return 1000 * (y + 1) + 4 * (x + 1) + facing
}


fun main() {
    val (map, instructions) = (DATAPATH / "2022/day22.txt").useLines { it.toList() }.let { lines ->
        lines.take(lines.size - 2) to parseInstructions(lines.last())
    }
    println("Part one: ${followInstructions1(map, instructions)}")
    println("Part two: ${followInstructions2(map, instructions)}")
}
