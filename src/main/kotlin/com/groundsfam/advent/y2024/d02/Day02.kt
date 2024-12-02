package com.groundsfam.advent.y2024.d02

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs

fun safeLevelChange(a: Int, b: Int, increasing: Boolean): Boolean =
    abs(a - b) in 1..3 && a < b == increasing

// is specifically used to check sub-lists of the main report
// with size >= 3
fun isSafe(report: List<Int>): Boolean {
    val increasing = report[0] < report[1]
    return (0..report.size - 2).all { i ->
        safeLevelChange(report[i], report[i + 1], increasing)
    }
}

fun isSafe(report: List<Int>, tolerateBadLevel: Boolean): Boolean {
    if (report.size <= 1) {
        return true
    }
    if (tolerateBadLevel && report.size == 2) {
        return true
    }
    if (tolerateBadLevel && report.size == 3) {
        val diffs = listOf(
            abs(report[0] - report[1]),
            abs(report[0] - report[2]),
            abs(report[1] - report[2]),
        )
        return diffs.any { it in 1..3 }
    }

    var i = 0
    // bit of a hack: if tolerateBadLevel = false, just pretend we already
    // saw and fixed a bad level
    var badLevelRemoved = !tolerateBadLevel
    var increasing = report[0] < report[1]
    while (i < report.size - 1) {
        when {
            report[i] == report[i + 1] -> {
                if (badLevelRemoved) {
                    return false
                }
                // special case: if these are the first two numbers in the report,
                // we must recompute the direction
                if (i == 0) {
                    increasing = report[1] < report[2]
                }

                // consider report[i] to be removed from the report
                // continue on as normal
                i++
                badLevelRemoved = true
            }

            (report[i] < report[i + 1]) != increasing -> {
                when {
                    badLevelRemoved -> {
                        return false
                    }

                    i == report.size - 2 -> {
                        return true
                    }

                    // special case: if this happens with the first three numbers of the report,
                    // we have to consider removing any of those three
                    // otherwise, we only consider removing the last two of those three
                    i == 1 -> {
                        val idxToSkip = listOf(0, 1, 2).firstOrNull { j ->
                            val sublist = (0..3)
                                .filterNot { it == j }
                                .map { report[it] }
                            isSafe(sublist)
                        } ?: return false

                        i = 3
                        badLevelRemoved = true
                        increasing = (0..2)
                            .filterNot { it == idxToSkip }
                            .let {
                                report[it[0]] < report[it[1]]
                            }
                    }

                    else -> {
                        val safeWithSkip = listOf(i, i + 1).any { j ->
                            // three numbers from report, skipping index j
                            val sublist = (i - 1..i + 2)
                                .filterNot { it == j }
                                .map { report[it] }
                            (0..sublist.size - 2).all {
                                safeLevelChange(sublist[it], sublist[it + 1], increasing)
                            }
                        }
                        if (!safeWithSkip) {
                            return false
                        }

                        // we verified safeness up to index i + 2
                        i += 2
                        badLevelRemoved = true
                    }
                }
            }

            abs(report[i] - report[i + 1]) > 3 -> {
                when {
                    badLevelRemoved -> {
                        return false
                    }

                    // special case: if this happens with the first two numbers of the report,
                    // we can possibly remove the first number and also possibly reverse
                    // increasing/decreasing
                    i == 0 -> {
                        val idxToKeep = listOf(0, 1).firstOrNull { j ->
                            val sublist = listOf(j, 2, 3).map { report[it] }
                            isSafe(sublist)
                        } ?: return false

                        i = 3
                        badLevelRemoved = true
                        increasing = report[idxToKeep] < report[idxToKeep + 1]
                    }

                    // otherwise, we must remove the second number
                    i == report.size - 2 || safeLevelChange(report[i], report[i + 2], increasing) -> {
                        i += 2
                        badLevelRemoved = true
                    }

                    else -> {
                        return false
                    }
                }
            }

            else -> {
                // this step is safe
                i++
            }
        }
    }
    return true
}

fun main() = timed {
    val reports = (DATAPATH / "2024/day02.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            line.split("""\s+""".toRegex())
                .map(String::toInt)
        }
    }
    reports.count { isSafe(it, false) }
        .also { println("Part one: $it") }
    reports.count { isSafe(it, true) }
        .also { println("Part two: $it") }
}
