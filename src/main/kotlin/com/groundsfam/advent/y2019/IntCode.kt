package com.groundsfam.advent.y2019

import com.groundsfam.advent.pow
import java.nio.file.Path
import kotlin.io.path.readText

fun Path.readProgram(): List<Int> = readText()
    .trim()
    .split(",")
    .map(String::toInt)

class IntCodeComputer(private val initProgram: List<Int>) {
    val memory = initProgram.toIntArray()

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

            fun getParam(i: Int): Int = memory[getParamLocation(i)]

            when (val op = num % 100) {
                1, 2 -> {
                    val a = getParam(1)
                    val b = getParam(2)
                    memory[getParamLocation(3)] =
                        if (op == 1) a + b
                        else a * b
                    ip += 4
                }

                3 -> {
                    memory[getParamLocation(1)] = input[inIdx++]
                    ip += 2
                }

                4 -> {
                    output.add(getParam(1))
                    ip += 2
                }

                5, 6 -> {
                    val a = getParam(1)
                    val b = getParam(2)
                    if ((a == 0) == (op == 6)) {
                        ip = b
                    } else {
                        ip += 3
                    }
                }

                7, 8 -> {
                    val a = getParam(1)
                    val b = getParam(2)
                    memory[getParamLocation(3)] =
                        if ((op == 7 && a < b) || (op == 8 && a == b)) 1
                        else 0
                    ip += 4
                }

                else -> throw RuntimeException("Invalid operation $op, instructionPointer=$ip")
            }
        }

        return output
    }
}
