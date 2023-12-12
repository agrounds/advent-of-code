package com.groundsfam.advent.y2021.d01

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


fun increasingDepths(depths: List<Int>) =
    (1 until depths.size)
        .count { i -> depths[i-1] < depths[i] }

fun increasingWindows(depths: List<Int>): Int {
    val windows = (2 until depths.size).map { i ->
        depths[i - 2] + depths[i - 1] + depths[i]
    }
    return increasingDepths(windows)
}

fun main() = timed {
    val depths = (DATAPATH / "2021/day01.txt").useLines { lines ->
        lines.toList()
            .map { it.toInt() }
    }
    println("Part one: ${increasingDepths(depths)}")
    println("Part two: ${increasingWindows(depths)}")
}

