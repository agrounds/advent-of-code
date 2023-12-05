package com.groundsfam.advent.y2022.d04

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


fun redundantPair(pair: List<Int>): Boolean =
    (pair[0] <= pair[2] && pair[1] >= pair[3]) || (pair[2] <= pair[0] && pair[3] >= pair[1])


fun overlappingPair(pair: List<Int>): Boolean =
    (pair[1] >= pair[2] && pair[0] <= pair[3]) || (pair[3] >= pair[0] && pair[2] <= pair[1])


fun main() = timed {
    // Pairs is a list of lists of ints. Each pair is a list of precisely four ints
    val pairs = (DATAPATH / "2022/day04.txt").useLines { lines ->
        lines.toList().map { line ->
            val (first, second) = line.split(',')
            first.split('-').map { it.toInt() } + second.split('-').map { it.toInt() }
        }
    }
    pairs.count(::redundantPair)
        .also { println("Redundant pairs: $it") }
    pairs.count(::overlappingPair)
        .also { println("Overlapping pairs: $it") }
}
