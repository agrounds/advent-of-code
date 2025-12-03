package com.groundsfam.advent.y2025.d02

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.divisors
import com.groundsfam.advent.gcd
import com.groundsfam.advent.max
import com.groundsfam.advent.min
import com.groundsfam.advent.numDigits
import com.groundsfam.advent.pow
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readText


fun numDigitsInRange(range: LongRange): IntRange =
    numDigits(range.first)..numDigits(range.last)


// sum of all invalid IDs that have [numDigits] and repeat the first [repeatLen] digits
// e.g. 121212 has numDigits = 6, repeatLen = 2
// e.g. 222222 has numDigits = 6, and repeatLen = 1, 2, or 3
// assumption: repeatLen is a proper divisor of numDigits
fun sumInvalidIDs(range: LongRange, numDigits: Int, repeatLen: Int): Long {
    val factor = (10.pow(numDigits) - 1) / (10.pow(repeatLen) - 1)
    val a = max(range.first / 10.pow(numDigits - repeatLen), 10.pow(repeatLen - 1)).let {
        if (it * factor in range) it
        else it + 1
    }
    val b = min(range.last / 10.pow(numDigits - repeatLen), 10.pow(repeatLen) - 1).let {
        if (it * factor in range) it
        else it - 1
    }

    return if (b < a) {
        0
    } else {
        // truncated triangle number times the factor 100...01
        (a + b) * (b - a + 1) / 2 * factor
    }
}


fun partOne(range: LongRange): Long =
    numDigitsInRange(range).sumOf { nd ->
        if (nd % 2 == 0) sumInvalidIDs(range, nd, nd / 2)
        else 0
    }


fun partTwo(range: LongRange): Long =
    numDigitsInRange(range).sumOf { nd ->
        // proper divisors of the number of digits of an invalid ID
        val divs = divisors(nd).filter { it < nd }.iterator()
        // gcd of all divisors seen so far
        var currGcd: Int? = null
        var sum = 0L
        while ((currGcd ?: 0) < nd && divs.hasNext()) {
            val d = divs.next()
            sum += sumInvalidIDs(range, nd, d)
            if (currGcd == null) {
                currGcd = d
            } else {
                currGcd = gcd(d, currGcd)
                // subtract portion of sum that we already got from previous divisors
                sum -= sumInvalidIDs(range, nd, currGcd)
            }
        }
        sum
    }


fun main() = timed {
    val ranges = (DATAPATH / "2025/day02.txt").readText().let { line ->
        line.trim().split(",").map { range ->
            val (from, to) = range.split("-").map(String::toLong)
            from..to
        }
    }
    ranges
        .sumOf(::partOne)
        .also { println("Part one: $it") }
    ranges
        .sumOf(::partTwo)
        .also { println("Part two: $it") }
}
