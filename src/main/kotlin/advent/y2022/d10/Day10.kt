package advent.y2022.d10

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

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

    instructions.forEach { instruction ->
        when (instruction) {
            is Noop -> {
                updateStrength()
                currentCycle++
            }
            is Addx -> {
                updateStrength()
                currentCycle++
                updateStrength()
                currentCycle++
                x += instruction.value
            }
        }
    }

    return totalStrength
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
}
