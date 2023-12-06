package com.groundsfam.advent.y2023.d06

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readLines
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.pow
import kotlin.math.sqrt

data class Race(val time: Long, val recordDistance: Long)

fun winningRange(race: Race): LongRange {
    // the amount of time (s) to hold down the button to tie the record
    // satisfies the equation -s^2 + st - r = 0
    // where t is the race time, and r is the record distance
    val t = race.time
    val r = race.recordDistance

    fun test(s: Long): Long = s * (t - s) - r

    val discriminant = t.toDouble().pow(2) - 4 * r
    val lowSolution = ceil((t - sqrt(discriminant)) / 2).toLong()
    val highSolution = floor((t + sqrt(discriminant)) / 2).toLong()

    // find integers that beat, not tie, the record, by starting with the approximate
    // solutions and iterating until the best solution to the strict inequality is found
    //
    // for my input, this was unnecessary -- lowSolution and highSolution turned out to be the
    // ideal solutions, so double precision arithmetic was good enough to get them on the first
    // try. it is possible that other inputs would require this extra work though
    val firstIntSolution =
        if (test(lowSolution) <= 0) {
            generateSequence(lowSolution) { it + 1 }
                .first { test(it) > 0 }
        } else {
            generateSequence(lowSolution) { it - 1 }
                .first { test(it - 1) <= 0 }
        }
    val lastIntSolution =
        if (test(highSolution) <= 0) {
            generateSequence(highSolution) { it - 1 }
                .first { test(it) > 0 }
        } else {
            generateSequence(highSolution) { it + 1 }
                .first { test(it + 1) <= 0 }
        }

    return firstIntSolution..lastIntSolution
}

fun main() = timed {
    val races = (DATAPATH / "2023/day06.txt")
        .readLines()
        .map { line ->
            line.split("""\s+""".toRegex())
                .drop(1)  // drop the prefix
                .map(String::toLong)
        }.let { (times, distances) ->
            // there are exactly two rows, each with the same number of numbers
            times.mapIndexed { i, time -> Race(time, distances[i]) }
        }
    races
        .map(::winningRange)
        .fold(1L) { p, winRange ->
            p * (winRange.last - winRange.first + 1)
        }
        .also { println("Part one: $it") }
    val combinedTime = races.map { it.time }.joinToString("").toLong()
    val combinedRecord = races.map { it.recordDistance }.joinToString("").toLong()
    winningRange(Race(combinedTime, combinedRecord))
        .let { it.last - it.first + 1 }
        .also { println("Part two: $it") }
}
