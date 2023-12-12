package com.groundsfam.advent.y2023.d12

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import java.lang.RuntimeException
import kotlin.io.path.div
import kotlin.io.path.useLines

// e.g. SpringRecord(".??..??...?##.", listOf(1, 1, 3))
data class SpringRecord(val row: String, val brokenCounts: List<Int>)

fun springArrangements(row: String, brokenGroupLengths: List<Int>): Int {
    // cache[i][j], if set, equals the number of arrangements of springs
    // for the substring row[i..] and the sublist brokenCounts[j..]
    val cache = Array(row.length) {
        IntArray(brokenGroupLengths.size + 1) { -1 }
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

    fun compute(i: Int, j: Int): Int {
        if (i == row.length) {
            return if (j == brokenGroupLengths.size) 1 else 0
        }

        if (cache[i][j] != -1) {
            return cache[i][j]
        }

        fun computeWorking(): Int =
            compute(i + 1, j)

        fun computeBroken(): Int =
            if (j == brokenGroupLengths.size) {
                0
            } else {
                // index of the end of the group, exclusive
                val endGroupIdx = i + brokenGroupLengths[j]

                when {
                    !brokenGroupPossible(i, endGroupIdx) -> 0

                    endGroupIdx == row.length -> {
                        // reached end of row -- this is a successful arrangement precisely when
                        // there are no more groups to build
                        if (j == brokenGroupLengths.size - 1) 1 else 0
                    }

                    else -> {
                        // set i to position after end of this group, including the working spring
                        // that ends this group, and increment j
                        compute(endGroupIdx + 1, j + 1)
                    }
                }
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
    springRecords.sumOf { (row, counts) ->
        springArrangements(row, counts)
    }
        .also { println("Part one: $it") }
}
