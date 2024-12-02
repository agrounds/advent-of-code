package com.groundsfam.advent.y2024.d02

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs

fun isSafe(report: List<Int>): Boolean {
    if (report.size < 2) {
        return true
    }
    val increasing = report[0] < report[1]
    (0..report.size - 2).forEach { i ->
        val a = report[i]
        val b = report[i + 1]
        if ((a < b) != increasing) {
            return false
        }
        if (abs(a - b) !in 1..3) {
            return false
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
    println("Part one: ${reports.count(::isSafe)}")
}