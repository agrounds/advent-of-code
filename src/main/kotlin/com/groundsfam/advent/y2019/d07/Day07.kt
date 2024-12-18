package com.groundsfam.advent.y2019.d07

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.generatePermutations
import com.groundsfam.advent.timed
import com.groundsfam.advent.y2019.IntCodeComputer
import com.groundsfam.advent.y2019.IntCodeState
import com.groundsfam.advent.y2019.readProgram
import kotlin.io.path.div
import kotlin.math.max

fun maxThrusterSignal(program: List<Int>, partTwo: Boolean): Int {
    val computers = Array(5) { IntCodeComputer(program) }
    var maxSignal = 0
    val phaseSettings =
        if (partTwo) (5..9).toList()
        else (0..4).toList()

    generatePermutations(phaseSettings).forEach { nums ->
        var output = 0

        computers.forEachIndexed { i, computer ->
            computer.reset()
            computer.sendInput(nums[i])
        }
        do {
            computers.forEach { computer ->
                computer.sendInput(output)
                computer.runProgram()
                output = computer.getOutput() ?: throw RuntimeException("No output")
            }
        } while (partTwo && computers.last().state != IntCodeState.FINISHED)

        maxSignal = max(output, maxSignal)
    }
    return maxSignal
}

fun main() = timed {
    val program = (DATAPATH / "2019/day07.txt").readProgram()
    println("Part one: ${maxThrusterSignal(program, false)}")
    println("Part one: ${maxThrusterSignal(program, true)}")
}
