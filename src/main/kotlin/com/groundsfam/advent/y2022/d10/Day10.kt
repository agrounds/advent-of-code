package com.groundsfam.advent.y2022.d10

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.y2015.d07.MAX_SIGNAL
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs

sealed class Instruction
object Noop : Instruction()
data class Addx(val value: Int) : Instruction()

fun signalStrengths(instructions: List<Instruction>): Int {
    var currentCycle = 1
    var totalStrength = 0
    var x = 1

    fun updateStrength() {
        if (currentCycle % 40 == 20)
            totalStrength += currentCycle * x
    }

    instructions.forEach {
        when (it) {
            is Noop -> {
                updateStrength()
                currentCycle++
            }
            is Addx -> {
                updateStrength()
                currentCycle++
                updateStrength()
                currentCycle++
                x += it.value
            }
        }
    }

    return totalStrength
}

fun simulateCRT(instructions: List<Instruction>): String {
    val screen = Array(6) { CharArray(40) { Character.MIN_VALUE } }
    var i = 0
    var j = 0
    var x = 1

    fun step() {
        screen[j][i] =
            if (abs(x - i) <= 1) '#'
            else '.'
        i++
        if (i == 40) {
            i = 0
            j++
        }
    }

    instructions.forEach {
        when (it) {
            is Noop ->
                step()
            is Addx -> {
                step()
                step()
                x += it.value
            }
        }
    }

    return screen.joinToString("\n") { String(it) }
}


fun main() {
    val instructions = (DATAPATH / "2022/day10.txt").useLines { lines ->
        lines.toList().map { line ->
            val parts = line.split(" ")
            when (parts[0]) {
                "noop" -> Noop
                "addx" -> Addx(parts[1].toInt())
                else -> throw RuntimeException("Illegal instruction: $line")
            }
        }
    }

    signalStrengths(instructions)
        .also { println("Part one: $it") }
    simulateCRT(instructions)
        .also { println("Part two:\n$it") }
}
