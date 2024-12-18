package com.groundsfam.advent.y2019.d07

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.generatePermutations
import com.groundsfam.advent.timed
import com.groundsfam.advent.y2019.IntCodeComputer
import com.groundsfam.advent.y2019.IntCodeState
import com.groundsfam.advent.y2019.readProgram
import kotlin.io.path.div
import kotlin.math.max

fun maxThrusterSignal(program: List<Long>, partTwo: Boolean): Long {
    val computers = Array(5) { IntCodeComputer(program) }
    var maxSignal: Long = 0
    val phaseSettings =
        if (partTwo) (5L..9L).toList()
        else (0L..4L).toList()

    generatePermutations(phaseSettings).forEach { nums ->
        var output: Long = 0

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
