package com.groundsfam.advent.y2023.d01

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


val englishDigits = mapOf(
    "one" to 1,
    "two" to 2,
    "three" to 3,
    "four" to 4,
    "five" to 5,
    "six" to 6,
    "seven" to 7,
    "eight" to 8,
    "nine" to 9,
)
fun String.getDigit(idx: Int): Int? {
    if (this[idx].isDigit()) {
        return this[idx].digitToInt()
    }
    englishDigits.keys.forEach { name ->
        if (idx + name.length <= this.length && this.substring(idx until idx + name.length) == name) {
            return englishDigits[name]
        }
    }
    return null
}

fun getCalibration1(line: String) = "${line.first { it.isDigit() }}${line.last { it.isDigit() }}".toInt()
fun getCalibration2(line: String): Int {
    val digits = line.indices.mapNotNull { line.getDigit(it) }
    return digits.first() * 10 + digits.last()
}

fun main() {
    val lines = (DATAPATH / "2023/day01.txt").useLines {
        it.toList()
    }
    lines
        .sumOf { getCalibration1(it) }
        .also { println("Part one: $it") }
    lines
        .sumOf { getCalibration2(it) }
        .also { println("Part two: $it") }
}
