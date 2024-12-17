package com.groundsfam.advent.y2019

import com.groundsfam.advent.numDigits
import com.groundsfam.advent.pow
import java.nio.file.Path
import kotlin.io.path.readText

fun Path.readProgram(): List<Int> = readText()
    .trim()
    .split(",")
    .map(String::toInt)

class IntCodeComputer(private val initProgram: List<Int>) {
    val memory = initProgram.toMutableList()
    // instruction pointer
    private var ip = 0

    fun reset() {
        initProgram.forEachIndexed { i, n ->
            memory[i] = n
        }
        ip = 0
    }

    fun runProgram(input: List<Int> = emptyList()): List<Int> {
        var inIdx = 0
        val output = mutableListOf<Int>()

        while (memory[ip] != 99) {
            val num = memory[ip]

            // 1-indexed params
            fun getParamLocation(i: Int): Int =
                when (val mode = (num / 10.pow(1 + i)).toInt() % 10) {
                    0 -> memory[ip + i]
                    1 -> ip + i
                    else -> throw RuntimeException("Invalid param mode $mode, instructionPointer=$ip")
                }

            when (val op = num % 100) {
                1 -> {
                    val (a, b, dest) = (1..3).map(::getParamLocation)
                    memory[dest] = memory[a] + memory[b]
                    ip += 4
                }
                2 -> {
                    val (a, b, dest) = (1..3).map(::getParamLocation)
                    memory[dest] = memory[a] * memory[b]
                    ip += 4
                }
                3 -> {
                    val dest = getParamLocation(1)
                    memory[dest] = input[inIdx]
                    inIdx++
                    ip += 2
                }
                4 -> {
                    val dest = getParamLocation(1)
                    output.add(memory[dest])
                    ip += 2
                }
                else -> throw RuntimeException("Invalid operation $op, instructionPointer=$ip")
            }
        }

        return output
    }
}
