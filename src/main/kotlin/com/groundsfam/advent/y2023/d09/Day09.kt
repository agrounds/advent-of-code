package com.groundsfam.advent.y2023.d09

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


fun extrapolate(history: List<Long>, backward: Boolean): Long {
    if (history.all { it == 0L }) {
        return 0
    }
    val diffs = (1 until history.size).map { i ->
        history[i] - history[i - 1]
    }
    val nextDiff = extrapolate(diffs, backward)
    return if (backward) {
        history.first() - nextDiff
    } else {
        history.last() + nextDiff
    }
}

fun main() = timed {
    val readings = (DATAPATH / "2023/day09.txt").useLines { lines ->
        lines
            .map { line ->
                line.split(" ")
                    .map(String::toLong)
            }
            .toList()
    }
    readings
        .sumOf { history -> extrapolate(history, backward = false) }
        .also { println("Part one: $it") }
    readings
        .sumOf { history -> extrapolate(history, backward = true) }
        .also { println("Part two: $it") }

}
