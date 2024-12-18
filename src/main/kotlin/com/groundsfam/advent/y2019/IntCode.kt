package com.groundsfam.advent.y2019

import com.groundsfam.advent.pow
import com.groundsfam.advent.y2019.IntCodeState.*
import java.nio.file.Path
import kotlin.io.path.readText

fun Path.readProgram(): List<Long> = readText()
    .trim()
    .split(",")
    .map(String::toLong)

enum class IntCodeState {
    NOT_STARTED,
    RUNNING,
    WAIT_FOR_INPUT,
    FINISHED,
}

class IntCodeComputer(private val initProgram: List<Long>) {
    val memory = mutableMapOf<Long, Long>()

    // instruction pointer
    private var ip: Long = 0
    private var relativeBase: Long = 0
    var state: IntCodeState = NOT_STARTED
        private set
    private val input = ArrayDeque<Long>()
    private val output = ArrayDeque<Long>()

    init {
        reset()
    }

    fun reset() {
        initProgram.forEachIndexed { i, n ->
            memory[i.toLong()] = n
        }
        ip = 0
        relativeBase = 0
        state = NOT_STARTED
        input.clear()
        output.clear()
    }

    // 1-indexed params
    private fun getParamLocation(num: Long, i: Int): Long =
        when (val mode = (num / 10.pow(1 + i)) % 10) {
            0L -> memory[ip + i]!!
            1L -> ip + i
            2L -> memory[ip + i]!! + relativeBase
            else -> throw RuntimeException("Invalid param mode $mode, instructionPointer=$ip")
        }

    private fun getParam(num: Long, i: Int): Long =
        memory[getParamLocation(num, i)] ?: 0

    fun sendInput(num: Long) {
        input.add(num)
    }

    fun sendInput(nums: Iterable<Long>) {
        input.addAll(nums)
    }

    fun getOutput(): Long? =
        if (output.isEmpty()) null
        else output.removeFirst()

    fun getAllOutput(): List<Long> =
        output.toList()
            .also { output.clear() }

    fun runProgram() {
        if (state == FINISHED) {
            throw RuntimeException("Cannot run program, execution is finished")
        }
        state = RUNNING

        while (state == RUNNING) {
            val num = memory[ip]!!

            when (val op = (num % 100).toInt()) {
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
                    if ((a == 0L) == (op == 6)) {
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

                9 -> {
                    relativeBase += getParam(num, 1)
                    ip += 2
                }

                99 -> {
                    state = FINISHED
                }

                else -> throw RuntimeException("Invalid operation $op, instructionPointer=$ip")
            }
        }
    }
}
