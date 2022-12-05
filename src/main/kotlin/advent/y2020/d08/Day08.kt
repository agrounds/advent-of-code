package advent.y2020.d08

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

private data class Instruction(val op: Op, val arg: Int)

private enum class Op {
    ACC, JMP, NOP
}

private data class ProgramResult(val accumulator: Int, val isLoop: Boolean)

private fun executeProgram(program: List<Instruction>): ProgramResult {
    var accumulator = 0
    var next = 0
    val prevInstructions = mutableSetOf<Int>()

    while (next !in prevInstructions && next < program.size) {
        prevInstructions.add(next)

        val instruction = program[next]
        when (instruction.op) {
            Op.ACC -> {
                accumulator += instruction.arg
                next++
            }
            Op.JMP -> {
                next += instruction.arg
            }
            Op.NOP -> {
                // no op
                next++
            }
        }
    }

    return ProgramResult(accumulator, next in prevInstructions)
}

fun main() {
    val program = (DATAPATH / "2020/day08.txt").useLines { lines ->
        lines.map { line ->
            val tokens = line.split(" ")
            Instruction(Op.valueOf(tokens[0].uppercase()), tokens[1].toInt())
        }.toMutableList()
    }

    executeProgram(program)
        .also { println("Part one: $it") }

    for (i in program.indices) {
        val instruction = program[i]
        when (instruction.op) {
            Op.JMP -> {
                program[i] = instruction.copy(op = Op.NOP)
                val result = executeProgram(program)
                if (!result.isLoop) {
                    println("Part two: $result")
                    break
                }
                program[i] = instruction
            }
            Op.NOP -> {
                program[i] = instruction.copy(op = Op.JMP)
                val result = executeProgram(program)
                if (!result.isLoop) {
                    println("Part two: $result")
                    break
                }
                program[i] = instruction
            }
            else -> throw RuntimeException("Unhandled op")
        }
    }
}
