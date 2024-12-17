package com.groundsfam.advent.y2019.d05

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import com.groundsfam.advent.y2019.IntCodeComputer
import com.groundsfam.advent.y2019.readProgram
import kotlin.io.path.div

fun runDiagnostics(computer: IntCodeComputer, code: Int): Int =
    computer.runProgram(listOf(code)).last()

fun main() = timed {
    val computer = (DATAPATH / "2019/day05.txt").readProgram()
        .let(::IntCodeComputer)
    println("Part one: ${runDiagnostics(computer, 1)}")
    computer.reset()
    println("Part two: ${runDiagnostics(computer, 5)}")
}
