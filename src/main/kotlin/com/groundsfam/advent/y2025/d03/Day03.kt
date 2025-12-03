package com.groundsfam.advent.y2025.d03

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readLines


fun maxJoltage(bank: String, numDigits: Int): Long {
    var startFrom = 0
    return (0..<numDigits).map { i ->
        val subBank = bank.substring(startFrom..<(bank.length - numDigits + i + 1))
        val maxDigit = subBank.max()
        startFrom += subBank.indexOfFirst { it == maxDigit } + 1
        maxDigit
    }.joinToString("").toLong()
}


fun main() = timed {
    val banks = (DATAPATH / "2025/day03.txt").readLines()
    banks
        .sumOf { maxJoltage(it, 2) }
        .also { println("Part one: $it") }
    banks
        .sumOf { maxJoltage(it, 12) }
        .also { println("Part two: $it") }
}
