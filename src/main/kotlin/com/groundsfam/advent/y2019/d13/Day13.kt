package com.groundsfam.advent.y2019.d13

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.points.*
import com.groundsfam.advent.timed
import com.groundsfam.advent.y2019.IntCodeComputer
import com.groundsfam.advent.y2019.readProgram
import kotlin.io.path.div

// net update -- points not present do not change
// from previous loop
data class Update(
    val grid: Map<Point, Int>,
    val score: Long?,
)

fun IntCodeComputer.readUpdate(): Update =
    parseUpdate(getAllOutput())

fun parseUpdate(output: List<Long>): Update {
    val grid = mutableMapOf<Point, Int>()
    var score: Long? = null
    output
        .chunked(3)
        .forEach { nums ->
            val x = nums[0].toInt()
            val y = nums[1].toInt()
            if (x == -1 && y == 0) {
                score = nums[2]
            } else {
                grid[Point(x, y)] = nums[2].toInt()
            }
        }
    return Update(grid, score)
}

fun runGame(game: List<Long>): Int {
    // map from position to tileId
    val computer = IntCodeComputer(game)
    computer.runProgram()
    val (grid, _) = computer.readUpdate()
    return grid.count { (_, id) -> id == 2 }
}

fun playGame(game: List<Long>): Long {
    val grid = mutableMapOf<Point, Int>()
    var score: Long? = null
    val computer = IntCodeComputer(game)

    fun handleUpdate() {
        computer.readUpdate().let { (g, s) ->
            g.forEach { (p, id) ->
                grid[p] = id
            }
            if (s != null) {
                score = s
            }
        }
    }

    // enable free play
    computer.memory[0] = 2
    computer.runProgram()
    handleUpdate()

    while (grid.any { (_, id) -> id == 2 }) {
        val paddle = grid.entries.first { (_, id) -> id == 3 }.key.x
        val ball = grid.entries.first { (_, id) -> id == 4 }.key.x
        computer.sendInput(when {
            ball < paddle -> -1
            ball > paddle -> 1
            else -> 0
        })

        computer.runProgram()
        handleUpdate()
    }

    return score!!
}

fun main() = timed {
    val game = (DATAPATH / "2019/day13.txt").readProgram()
    println("Part one: ${runGame(game)}")
    println("Part two: ${playGame(game)}")
}
