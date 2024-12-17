package com.groundsfam.advent.y2024.d17

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.pow
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

enum class Instruction(val opCode: Int) {
    ADV(0),
    BXL(1),
    BST(2),
    JNZ(3),
    BXC(4),
    OUT(5),
    BDV(6),
    CDV(7),
}

fun Int.asInstruction(): Instruction? =
    Instruction.entries.firstOrNull { it.opCode == this }

class Solution(private val program: List<Int>) {
    // [A, B, C]
    private val registers = LongArray(3)
    // instruction pointer
    private var ip = 0
    private val output = mutableListOf<Int>()

    private fun reset() {
        registers.indices.forEach {
            registers[it] = 0
        }
        ip = 0
        output.clear()
    }

    private fun comboOperand(i: Int): Long = when (i) {
        0,1,2,3 -> i.toLong()
        4,5,6 -> registers[i - 4]
        else -> throw RuntimeException("Invalid combo operand $i")
    }

    // run one loop of the program and stop
    private fun runLoop() {
        do {
            val instruction = program[ip].asInstruction()
                ?: throw RuntimeException("Invalid instruction ${program[ip]}, instructionPointer = $ip")
            val operand = program[ip + 1]
            when (instruction) {
                Instruction.ADV -> {
                    registers[0] = registers[0] / 2.pow(comboOperand(operand).toInt())
                }

                Instruction.BXL -> {
                    registers[1] = registers[1].xor(operand.toLong())
                }

                Instruction.BST -> {
                    registers[1] = comboOperand(operand) % 8
                }

                Instruction.JNZ -> {
                    if (registers[0] != 0L) {
                        // hack: allow ip += 2 to end up with correct value
                        ip = operand - 2
                    }
                }

                Instruction.BXC -> {
                    registers[1] = registers[1].xor(registers[2])
                }

                Instruction.OUT -> {
                    output.add((comboOperand(operand) % 8).toInt())
                }

                Instruction.BDV -> {
                    registers[1] = registers[0] / 2.pow(comboOperand(operand).toInt())
                }

                Instruction.CDV -> {
                    registers[2] = registers[0] / 2.pow(comboOperand(operand).toInt())
                }
            }

            ip += 2
        } while (ip in 1 until program.size)
    }

    fun runProgram(initRegisters: List<Long>): List<Int> {
        reset()

        registers.indices.forEach { i ->
            registers[i] = initRegisters[i]
        }

        while (ip in program.indices) {
            runLoop()
        }

        return output.toList()
    }

    // In my input, the program is as follows:
    // B = A % 8
    // B = B xor 3
    // C = A / 2^B
    // B = B xor C
    // A /= 8
    // B = B xor 5
    // output B
    // if A != 0 goto beginning
    //
    // On each loop, B and C are determined based on
    // the current value of A. Then B is output, and
    // A is divided by 8. The loop ends when A reaches
    // zero. So, thinking of A in octal, the last
    // output only depends on the first digit of A,
    // the last two outputs only depend on the first
    // two digits of A, and so on. We can therefore
    // work backwards to determine, for each n, the
    // first n digits of A from the nth-to-last output
    // and the known possible first n-1 digits of A.
    fun findSelfOutputtingProgram(): Long {
        var possibleA = listOf(0L)

        program.reversed().forEach { n ->
            val nextA = mutableListOf<Long>()
            possibleA.forEach { a ->
                // edge case: first octal digit of A cannot be zero
                val ks = if (a == 0L) 1..7 else 0..7
                ks.forEach { k ->
                    reset()
                    registers[0] = a * 8 + k
                    runLoop()
                    if (n == output[0]) {
                        nextA.add(a * 8 + k)
                    }
                }
            }
            possibleA = nextA
        }

        return possibleA.min()
    }
}

fun main() = timed {
    val (initRegisters, solution) = (DATAPATH / "2024/day17.txt").useLines { lines ->
        val iter = lines.iterator()
        val initRegisters = mutableListOf<Long>()
        repeat(3) {
            iter.next()
                .substring(12)
                .toLong()
                .also(initRegisters::add)
        }
        iter.next()
        val program = iter.next()
            .substring(9)
            .split(',')
            .map { it.toInt() }
        Pair(initRegisters, Solution(program))
    }
    solution.runProgram(initRegisters)
        .joinToString(",")
        .also { println("Part one: $it") }
    println("Part two: ${solution.findSelfOutputtingProgram()}")
}
