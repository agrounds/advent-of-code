package com.groundsfam.advent.y2025.d05

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.rangeUnion
import com.groundsfam.advent.size
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


fun unionRanges(ranges: List<LongRange>): List<LongRange> {
    val sorted = ranges.sortedBy { it.first }
    val ret = mutableListOf<LongRange>()
    sorted.forEach { range ->
        val union = ret.lastOrNull()?.let {
            range.rangeUnion(it)
        }
        if (union != null) {
            ret[ret.size - 1] = union
        } else {
            ret.add(range)
        }
    }
    return ret
}


fun main() = timed {
    val (freshRanges, ingredientIDs) = (DATAPATH / "2025/day05.txt").useLines { lines ->
        val ranges = mutableListOf<LongRange>()
        val ids = mutableListOf<Long>()
        var isRange = true
        lines.forEach { line ->
            when {
                line.isBlank() -> {
                    isRange = false
                }
                isRange -> {
                    val (a, b) = line.split("-")
                    ranges.add(a.toLong() .. b.toLong())
                }
                else -> {
                    ids.add(line.toLong())
                }
            }
        }
        unionRanges(ranges) to ids
    }
    ingredientIDs.count { iid -> freshRanges.any { iid in it } }
        .also { println("Part one: $it") }
    freshRanges.sumOf { it.size }
        .also { println("Part two: $it") }
}
