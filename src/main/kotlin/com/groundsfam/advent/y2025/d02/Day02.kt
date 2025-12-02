package com.groundsfam.advent.y2025.d02

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.numDigits
import com.groundsfam.advent.pow
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readText


fun firstHalfOf(n: Long): Long {
    val nd = numDigits(n)
    return if (nd % 2 == 0) {
        n / 10.pow(nd / 2)
    } else {
        10.pow((nd - 1) / 2)
    }
}


fun findInvalidIDs(range: LongRange): List<Long> {
    val a = firstHalfOf(range.first)
    val b = firstHalfOf(range.last)

    return (a..b).mapNotNull { n ->
        "$n$n".toLong().takeIf { it in range }
    }
}


fun main() = timed {
    val ranges = (DATAPATH / "2025/day02.txt").readText().let { line ->
        line.trim().split(",").map { range ->
            val (from, to) = range.split("-").map(String::toLong)
            from..to
        }
    }
    ranges
        .sumOf { range ->
            findInvalidIDs(range).sum()
        }
        .also { println("Part one: $it") }
}
