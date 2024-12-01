package com.groundsfam.advent.y2024.d01

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs


fun distance(left: List<Int>, right: List<Int>): Int =
    left.sorted()
        .zip(right.sorted())
        .sumOf { (a, b) -> abs(a - b) }

fun similarity(left: List<Int>, right: List<Int>): Int {
    val rightCounts = right.groupBy { it }
        .mapValues { (_, v) -> v.size }

    return left.toSet().sumOf { n ->
        n * rightCounts.getOrDefault(n, 0)
    }
}

fun main() = timed {
    val left = mutableListOf<Int>()
    val right = mutableListOf<Int>()
    (DATAPATH / "2024/day01.txt").useLines { lines ->
        val r = """(\d+)\s+(\d+)""".toRegex()
        lines.forEach { line ->
            val (a, b) = r.find(line)!!.destructured
            left.add(a.toInt())
            right.add(b.toInt())
        }
    }
    println("Part 1: ${distance(left, right)}")
    println("Part 2: ${similarity(left, right)}")
}
