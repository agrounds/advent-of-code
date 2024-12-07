package com.groundsfam.advent.y2024.d07

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.pow
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.log10

data class Calibration(val target: Long, val nums: List<Long>)

fun parseLine(line: String): Calibration {
    val parts = line.split(""":?\s""".toRegex())
    return Calibration(parts[0].toLong(), parts.takeLast(parts.size - 1).map(String::toLong))
}

fun isValid(calibration: Calibration, partTwo: Boolean): Boolean {
    val (target, nums) = calibration
    // t = target value
    // i = index of number to try to use in expression to reach t
    fun helper(t: Long, i: Int): Boolean {
        val n = nums[i]
        if (i == 0) {
            return n == t
        }
        // try to divide both sides by n, if possible
        if (t % n == 0L && helper(t / n, i - 1)) {
            return true
        }
        // try to un-concatenate n from both sides
        if (partTwo) {
            val pow10 = 10.pow(log10(n.toDouble()).toInt() + 1)
            if (t % pow10 == n && helper(t / pow10, i - 1)) {
                return true
            }
        }
        // try to subtract n from both sides
        return helper(t - n, i - 1)
    }
    return helper(target, nums.size - 1)
}

fun main() = timed {
    val calibrations = (DATAPATH / "2024/day07.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), ::parseLine)
    }
    calibrations
        .filter { isValid(it, false) }
        .sumOf { it.target }
        .also { println("Part one: $it") }
    calibrations
        .filter { isValid(it, true) }
        .sumOf { it.target }
        .also { println("Part two: $it") }
}
