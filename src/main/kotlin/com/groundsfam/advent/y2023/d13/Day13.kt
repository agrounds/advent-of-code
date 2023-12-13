package com.groundsfam.advent.y2023.d13

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

sealed interface Reflection

@JvmInline
value class Column(val n: Int) : Reflection

@JvmInline
value class Row(val n: Int) : Reflection


fun findReflection(pattern: List<List<Char>>, smudged: Boolean): Reflection? {

    // count of differing characters between two lists
    fun diffs(s1: List<Char>, s2: List<Char>): Int =
        s1.zip(s2).count { (c1, c2) ->c1 != c2 }

    (1 until pattern.size).firstOrNull { y ->
        var smudgesRemaining = if (smudged) 1 else 0
        val rowsMatch = (0 until y).all { y1 ->
            // row number of the reflection of y1 across y
            val y2 = 2 * y - 1 - y1
            // allows us to call diffs() even if y2 is too big
            val diff = if (y2 >= pattern.size) null else diffs(pattern[y1], pattern[y2])
            when (diff) {
                // y2 is too big -- there's nothing to compare this row to
                null -> true
                // rows are identical
                0 -> true
                // rows are identical up to a smudge
                1 -> {
                    smudgesRemaining--
                    true
                }
                // rows are different
                else -> false
            }
        }
        rowsMatch && smudgesRemaining == 0
    }?.also {
        return Row(it)
    }

    (1 until pattern[0].size).firstOrNull { x ->
        var smudgesRemaining = if (smudged) 1 else 0
        val colsMatch = (0 until x).all { x1 ->
            // column number of the reflection of x1 across x
            val x2 = 2 * x - 1 - x1
            // allows us to call diffs() even if x2 is too big
            val diff = x2
                .takeIf { it < pattern[0].size }
                ?.let {
                    val col1 = pattern.map { line -> line[x1] }
                    val col2 = pattern.map { line -> line[x2] }
                    diffs(col1, col2)
                }
            when (diff) {
                // x2 is too big -- there's nothing to compare this column to
                null -> true
                // columns are identical
                0 -> true
                // columns are identical up to a smudge
                1 -> {
                    smudgesRemaining--
                    true
                }
                // columns are different
                else -> false
            }
        }
        colsMatch && smudgesRemaining == 0
    }?.also {
        return Column(it)
    }

    return null
}



fun main() = timed {
    val patterns: List<List<List<Char>>> = (DATAPATH / "2023/day13.txt").useLines { lines ->
        val parsedPatterns = mutableListOf<List<List<Char>>>()
        var currPattern = mutableListOf<List<Char>>()

        lines.forEach { line ->
            if (line.isBlank()) {
                parsedPatterns.add(currPattern)
                currPattern = mutableListOf()
            } else {
                currPattern.add(line.toList())
            }
        }
        if (currPattern.isNotEmpty()) {
            parsedPatterns.add(currPattern)
        }

        parsedPatterns
    }

    patterns
        .sumOf { pattern ->
            when (val r = findReflection(pattern, smudged = false)) {
                is Row -> r.n * 100L
                is Column -> r.n.toLong()
                null -> throw RuntimeException("No reflection found for pattern $pattern")
            }
        }
        .also { println("Part one: $it") }
    patterns
        .sumOf { pattern ->
            when (val r = findReflection(pattern, smudged = true)) {
                is Row -> r.n * 100L
                is Column -> r.n.toLong()
                null -> throw RuntimeException("No smudged reflection found for pattern $pattern")
            }
        }
        .also { println("Part two: $it") }
}
