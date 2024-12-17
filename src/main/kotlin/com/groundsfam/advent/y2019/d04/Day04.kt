package com.groundsfam.advent.y2019.d04

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.pow
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readText

fun meetsPasswordRequirements(password: Int, partTwo: Boolean): Boolean {
    // this is a six digit number
    val digits = (1..6).map { (password / 10.pow(6 - it)) % 10 }

    var adjacentRepeat = false
    (0 until digits.size - 1).forEach { i ->
        val d1 = digits[i]
        val d2 = digits[i + 1]
        if (d1 > d2) return false
        if (d1 == d2) {
            val prevDiff = i == 0 || digits[i - 1] != d1
            val postDiff = i == digits.size - 2 || digits[i + 2] != d1
            if (!partTwo || (prevDiff && postDiff)) {
                adjacentRepeat = true
            }
        }
    }
    return adjacentRepeat
}

fun main() = timed {
    val range = (DATAPATH / "2019/day04.txt")
        .readText()
        .trim()
        .split("-")
        .let { (a, b) -> a.toInt()..b.toInt() }
    println("Part one: ${range.count { meetsPasswordRequirements(it, false) }}")
    println("Part two: ${range.count { meetsPasswordRequirements(it, true) }}")
}
