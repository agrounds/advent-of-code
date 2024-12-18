package com.groundsfam.advent.y2019

import com.groundsfam.advent.pow
import com.groundsfam.advent.y2019.IntCodeState.*
import java.nio.file.Path
import kotlin.io.path.readText

fun Path.readProgram(): List<Int> = readText()
    .trim()
    .split(",")
    .map(String::toInt)

enum class IntCodeState {
    NOT_STARTED,
    RUNNING,
    WAIT_FOR_INPUT,
    FINISHED,
}

class IntCodeComputer(private val initProgram: List<Int>) {
    val memory = initProgram.toIntArray()

    // instruction pointer
    private var ip = 0
    var state: IntCodeState = NOT_STARTED
        private set
    private val input = ArrayDeque<Int>()
    private val output = ArrayDeque<Int>()

    fun reset() {
        initProgram.forEachIndexed { i, n ->
            memory[i] = n
        }
        ip = 0
        state = NOT_STARTED
        input.clear()
        output.clear()
    }

    // 1-indexed params
    private fun getParamLocation(num: Int, i: Int): Int =
        when (val mode = (num / 10.pow(1 + i)).toInt() % 10) {
            0 -> memory[ip + i]
            1 -> ip + i
            else -> throw RuntimeException("Invalid param mode $mode, instructionPointer=$ip")
        }

    private fun getParam(num: Int, i: Int): Int = memory[getParamLocation(num, i)]

    fun sendInput(num: Int) {
        input.add(num)
    }

    fun sendInput(nums: Iterable<Int>) {
        input.addAll(nums)
    }

    fun getOutput(): Int? =
        if (output.isEmpty()) null
        else output.removeFirst()

    fun getAllOutput(): List<Int> =
        output.toList()
            .also { output.clear() }

    fun runProgram() {
        if (state == FINISHED) {
            throw RuntimeException("Cannot run program, execution is finished")
        }
        state = RUNNING

        while (state == RUNNING) {
            val num = memory[ip]

            when (val op = num % 100) {
                1, 2 -> {
                    val a = getParam(num, 1)
                    val b = getParam(num, 2)
                    memory[getParamLocation(num, 3)] =
                        if (op == 1) a + b
                        else a * b
                    ip += 4
                }

                3 -> {
                    if (input.isEmpty()) {
                        state = WAIT_FOR_INPUT
                    } else {
                        memory[getParamLocation(num, 1)] = input.removeFirst()
                        ip += 2
                    }
                }

                4 -> {
                    output.add(getParam(num, 1))
                    ip += 2
                }

                5, 6 -> {
                    val a = getParam(num, 1)
                    val b = getParam(num, 2)
                    if ((a == 0) == (op == 6)) {
                        ip = b
                    } else {
                        ip += 3
                    }
                }

                7, 8 -> {
                    val a = getParam(num, 1)
                    val b = getParam(num, 2)
                    memory[getParamLocation(num, 3)] =
                        if ((op == 7 && a < b) || (op == 8 && a == b)) 1
                        else 0
                    ip += 4
                }

                99 -> {
                    state = FINISHED
                }

                else -> throw RuntimeException("Invalid operation $op, instructionPointer=$ip")
            }
        }
    }
}
