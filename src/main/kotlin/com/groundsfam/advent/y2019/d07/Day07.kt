package com.groundsfam.advent.y2019.d07

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.generatePermutations
import com.groundsfam.advent.timed
import com.groundsfam.advent.y2019.IntCodeComputer
import com.groundsfam.advent.y2019.readProgram
import kotlin.io.path.div
import kotlin.math.max

fun maxThrusterSignal(program: List<Int>): Int {
    val computers = Array(5) { IntCodeComputer(program) }
    var maxSignal = 0
    generatePermutations((0..4).toList()).forEach { nums ->
        var output = 0
        computers.forEach { it.reset() }
        computers.forEachIndexed { i, computer ->
            computer.sendInput(listOf(nums[i], output))
            computer.runProgram()
            output = computer.getOutput() ?: throw RuntimeException("No output")
        }
        maxSignal = max(output, maxSignal)
    }
    return maxSignal
}

fun main() = timed {
    val program = (DATAPATH / "2019/day07.txt").readProgram()
    println("Part one: ${maxThrusterSignal(program)}")
}
