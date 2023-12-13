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


fun findReflection(pattern: List<String>): Reflection? {
    (1 until pattern.size).firstOrNull { y ->
        (0 until y).all { y1 ->
            // row number of the reflection of y1 across y
            val y2 = 2 * y - 1 - y1
            y2 >= pattern.size || pattern[y1] == pattern[y2]
        }
    }?.also {
        return Row(it)
    }

    (1 until pattern[0].length).firstOrNull { x ->
        (0 until x).all { x1 ->
            // column number of the reflection of x1 across x
            val x2 = 2 * x - 1 - x1
            // compare character by character since we're going down a column
            pattern.all { line ->
                x2 >= line.length || line[x1] == line[x2]
            }
        }
    }?.also {
        return Column(it)
    }

    return null
}



fun main() = timed {
    val patterns: List<List<String>> = (DATAPATH / "2023/day13.txt").useLines { lines ->
        val parsedPatterns = mutableListOf<List<String>>()
        var currPattern = mutableListOf<String>()

        lines.forEach { line ->
            if (line.isBlank()) {
                parsedPatterns.add(currPattern)
                currPattern = mutableListOf()
            } else {
                currPattern.add(line)
            }
        }
        if (currPattern.isNotEmpty()) {
            parsedPatterns.add(currPattern)
        }

        parsedPatterns
    }

    patterns
        .sumOf { pattern ->
            when (val r = findReflection(pattern)) {
                is Row -> r.n * 100L
                is Column -> r.n.toLong()
                null -> throw RuntimeException("No reflection found for pattern $pattern")
            }
        }
        .also { println("Part one: $it") }
}
