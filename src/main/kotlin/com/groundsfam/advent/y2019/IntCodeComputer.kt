package com.groundsfam.advent.y2019

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

    fun runProgram() {
        while (memory[ip] != 99) {
            val (op, a, b, out) = (ip..(ip + 3)).map { memory[it] }
            when (op) {
                1 -> memory[out] = memory[a] + memory[b]
                2 -> memory[out] = memory[a] * memory[b]
                else -> throw RuntimeException("Invalid operation $op, instructionPointer=$ip")
            }
            ip += 4
        }
    }
}
