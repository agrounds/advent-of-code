package com.groundsfam.advent.y2019.d01

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

fun fuel(mass: Int, partTwo: Boolean): Int {
    var total = mass / 3 - 2
    if (!partTwo) return total

    var prevFuel = total
    while (prevFuel / 3 - 2 > 0) {
        prevFuel = prevFuel / 3 - 2
        total += prevFuel
    }
    return total
}

fun main() = timed {
    val modules = (DATAPATH / "2019/day01.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), String::toInt)
    }
    modules
        .sumOf { fuel(it, false) }
        .also { println("Part one: $it") }
    modules
        .sumOf { fuel(it, true) }
        .also { println("Part two: $it") }
}
