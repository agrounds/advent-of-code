package com.groundsfam.advent.y2024.d03

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readText

fun runMemory(memory: String, withConditionals: Boolean): Int {
    var mulEnabled = true
    var sum = 0
    val r = """do\(\)|don't\(\)|mul\((\d{1,3}),(\d{1,3})\)""".toRegex()

    r.findAll(memory).forEach { res ->
        when (res.value) {
            "do()" -> {
                mulEnabled = true
            }

            "don't()" -> {
                mulEnabled = false
            }

            else -> {
                if (!withConditionals || mulEnabled) {
                    val (a, b) = res.destructured
                    sum += a.toInt() * b.toInt()
                }
            }
        }
    }

    return sum
}

fun main() = timed {
    val memory = (DATAPATH / "2024/day03.txt").readText()
    println("Part one: ${runMemory(memory, false)}")
    println("Part two: ${runMemory(memory, true)}")
}
