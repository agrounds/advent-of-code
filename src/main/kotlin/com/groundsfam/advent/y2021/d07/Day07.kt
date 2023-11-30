package com.groundsfam.advent.y2021.d07

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs


fun minFuelOne(positions: List<Int>): Int {
    val median = positions.sorted().let {
        it[it.size / 2]
    }
    return positions.sumOf { abs(it - median) }
}

fun minFuelTwo(positions: List<Int>): Int {
    // hunch: the best position to move to is the mean of the positions
    // in fact, the mean is _slightly_ wrong, see https://www.reddit.com/r/adventofcode/comments/xz2ebs/more_on_2021_day_7_part_2/
    // but the correct position is guaranteed to be within 1 of the rounded mean
    val mean = positions.sum() / positions.size
    return listOf(mean - 1, mean, mean + 1).minOf { target ->
        positions.sumOf { position ->
            val d = abs(position - target)
            // formula for triangle number
            d * (d + 1) / 2
        }
    }
}

fun main() {
    val positions = (DATAPATH / "2021/day07.txt").useLines { lines ->
        lines.first().split(",").map { it.toInt() }
    }
    println("Part one: ${minFuelOne(positions)}")
    println("Part two: ${minFuelTwo(positions)}")
}
