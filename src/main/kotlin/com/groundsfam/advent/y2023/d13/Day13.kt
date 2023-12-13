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

// count of differing characters between two lists
fun diffs(s1: List<Char>, s2: List<Char>): Int =
    s1.zip(s2).count { (c1, c2) -> c1 != c2 }

fun findReflection(pattern: List<List<Char>>, smudged: Boolean): Reflection? {
    fun line(n: Int, byRows: Boolean): List<Char> =
        if (byRows) {
            pattern[n]
        } else {
            pattern.map { line -> line[n] }
        }

    fun find(byRows: Boolean): Int? {
        val numLines = if (byRows) {
            pattern.size
        } else {
            pattern[0].size
        }
        return (1 until numLines).firstOrNull { a ->
            var smudgesRemaining = if (smudged) 1 else 0
            val linesMatch = (0 until a).all { a1 ->
                // line number of the reflection of a1 across a
                val a2 = 2 * a - 1 - a1
                // allows us to call diffs() even if a2 is too big
                val diff = if (a2 >= numLines) null else diffs(line(a1, byRows), line(a2, byRows))
                when (diff) {
                    // a2 is too big -- there's nothing to compare this line to
                    null -> true
                    // lines are identical
                    0 -> true
                    // lines are identical up to a smudge
                    1 -> {
                        smudgesRemaining--
                        true
                    }
                    // lines are different
                    else -> false
                }
            }
            linesMatch && smudgesRemaining == 0
        }
    }

    find(byRows = true)
        ?.also { return Row(it) }
    find(byRows = false)
        ?.also { return Column(it) }
    return null
}



fun main() = timed {
    // list of patterns
    // each pattern is a list of rows
    // each row is a list of characters, a list-ified string
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

    fun List<List<List<Char>>>.sumReflections(smudged: Boolean): Long =
        this.sumOf { pattern ->
            when (val r = findReflection(pattern, smudged)) {
                is Row -> r.n * 100L
                is Column -> r.n.toLong()
                null -> throw RuntimeException("No${if (smudged) " smudged" else ""} reflection found for pattern $pattern")
            }
        }

    patterns
        .sumReflections(smudged = false)
        .also { println("Part one: $it") }
    patterns
        .sumReflections(smudged = true)
        .also { println("Part two: $it") }
}
