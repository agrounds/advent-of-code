package com.groundsfam.advent.y2023.d12

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

// e.g. SpringRecord(".??..??...?##.", listOf(1, 1, 3))
data class SpringRecord(val row: String, val brokenCounts: List<Int>)

fun SpringRecord.unfold() = SpringRecord(
    (0 until 5).joinToString("?") { row },
    (0 until 5).flatMap { brokenCounts }
)

fun springArrangements(row: String, brokenGroupLengths: List<Int>): Long {
    // cache[i][j], if set, equals the number of arrangements of springs
    // for the substring row[i..] and the sublist brokenCounts[j..]
    val cache = Array(row.length) {
        LongArray(brokenGroupLengths.size + 1) { -1L }
    }

    // from inclusive, to exclusive
    fun brokenGroupPossible(from: Int, to: Int): Boolean = when {
        to > row.length -> {
            // not enough springs remaining
            false
        }

        to == row.length -> {
            // all in range must not be marked as working
            (from until to).all { row[it] != '.' }
        }

        else -> {
            // all in range must not be marked as working,
            // and the following spring must not be marked as broken
            (from until to).all { row[it] != '.' } && row[to] != '#'
        }
    }

    fun compute(i: Int, j: Int): Long {
        if (i == row.length) {
            return if (j == brokenGroupLengths.size) 1 else 0
        }

        if (cache[i][j] != -1L) {
            return cache[i][j]
        }

        fun computeWorking(): Long =
            compute(i + 1, j)

        fun computeBroken(): Long {
            if (j == brokenGroupLengths.size) {
                return 0
            }
            // index of the end of the group, exclusive
            val endGroupIdx = i + brokenGroupLengths[j]

            if (!brokenGroupPossible(i, endGroupIdx)) {
                return 0
            }
            if (endGroupIdx == row.length) {
                // reached end of row -- this is a successful arrangement precisely when
                // there are no more groups to build
                return if (j == brokenGroupLengths.size - 1) 1 else 0
            }
            // set i to position after end of this group, including the working spring
            // that ends this group, and increment j
            return compute(endGroupIdx + 1, j + 1)
        }

        return when (val c = row[i]) {
            '.' -> computeWorking()
            '#' -> computeBroken()
            '?' -> computeWorking() + computeBroken()
            else -> throw RuntimeException("Illegal character in spring record: $c")
        }
            .also { cache[i][j] = it }
    }

    return compute(0, 0)
}


fun main() = timed {
    val springRecords: List<SpringRecord> = (DATAPATH / "2023/day12.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            val (row, countsStr) = line.split(" ", limit = 2)
            val counts = countsStr.split(",").map(String::toInt)
            SpringRecord(row, counts)
        }
    }
    springRecords
        .sumOf { (row, counts) ->
            springArrangements(row, counts)
        }
        .also { println("Part one: $it") }
    springRecords
        .map(SpringRecord::unfold)
        .sumOf { (row, counts) ->
            springArrangements(row, counts)
        }
        .also { println("Part two: $it") }
}
