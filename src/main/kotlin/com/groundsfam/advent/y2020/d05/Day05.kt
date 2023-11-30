package com.groundsfam.advent.y2020.d05

import com.groundsfam.advent.DATAPATH
import java.lang.RuntimeException
import kotlin.io.path.div
import kotlin.io.path.useLines

private fun String.fromPlaneBinary(): Int = this.map { c ->
        when (c) {
            'F' -> 0
            'B' -> 1
            'L' -> 0
            'R' -> 1
            else -> throw RuntimeException("Illegal char: $c")
        }
    }.joinToString("").toInt(2)

fun main() {
    val seatNumbers = (DATAPATH / "2020/day05.txt").useLines { it.toList() }
        .map { it.fromPlaneBinary() }

    val max = seatNumbers.maxOrNull()
        ?.also { println("Part one: $it") }
        ?: throw RuntimeException("Empty list??")

    seatNumbers.toSet().let { set ->
        (max downTo 0).first { n ->
            (n !in set)
        }
    }.also { println("Part two: $it") }
}
