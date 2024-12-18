package com.groundsfam.advent.y2019.d02

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import com.groundsfam.advent.y2019.IntCodeComputer
import com.groundsfam.advent.y2019.readProgram
import kotlin.io.path.div

const val TARGET = 19690720L

fun runProgram(computer: IntCodeComputer, a: Long, b: Long): Long =
    computer.run {
        reset()
        memory[1] = a
        memory[2] = b
        runProgram()
        memory[0]!!
    }

fun findInputs(computer: IntCodeComputer): Long =
    (0L..99L).firstNotNullOf { a ->
        val b = (0L..99L).firstOrNull { b ->
            runProgram(computer, a, b) == TARGET
        }
        b?.let { 100 * a + b }
    }

fun main() = timed {
    val computer = (DATAPATH / "2019/day02.txt").readProgram()
        .let(::IntCodeComputer)
    println("Part one: ${runProgram(computer, 12, 2)}")
    println("Part two: ${findInputs(computer)}")
}
