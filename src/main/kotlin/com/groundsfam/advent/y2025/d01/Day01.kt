package com.groundsfam.advent.y2025.d01

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readLines

fun countZeros(turns: List<Int>, partTwo: Boolean): Int {
    var pos = 50
    var numZeros = 0
    turns.forEach { turn ->
        val nextPos = pos + turn
        // range of clicks during this turn
        val (begin, end) = if (turn > 0) {
            pos + 1 to nextPos
        } else {
            nextPos to pos - 1
        }

        if (partTwo) {
            // find the number of multiples of 100 in the range of clicks
            // b.floorDiv(100) - a.floorDiv(100) is the number of multiples of 100
            // that are between a and b, exclusive
            // use a = begin - 1 to count begin if it's a multiple of 100 itself
            numZeros += end.floorDiv(100) - (begin - 1).floorDiv(100)
        } else if (nextPos % 100 == 0) {
            numZeros++
        }
        pos = nextPos
    }
    return numZeros
}

fun main() = timed {
    val turns = (DATAPATH / "2025/day01.txt").readLines().map { line ->
        val amount = line.substring(1).toInt()
        if (line[0] == 'R') amount else -amount
    }
    println("Part one: ${countZeros(turns, false)}")
    println("Part two: ${countZeros(turns, true)}")
}
