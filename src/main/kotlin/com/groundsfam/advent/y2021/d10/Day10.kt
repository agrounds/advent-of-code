package com.groundsfam.advent.y2021.d10

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


val syntaxScores = mapOf(
    ')' to 3,
    ']' to 57,
    '}' to 1197,
    '>' to 25137,
)
val autocompleteScores = mapOf(
    ')' to 1,
    ']' to 2,
    '}' to 3,
    '>' to 4,
)

val openToClose = mapOf(
    '(' to ')',
    '[' to ']',
    '{' to '}',
    '<' to '>',
)

fun findIllegalChar(line: String): Int? {
    val stack = ArrayDeque<Char>()
    line.forEach { c ->
        if (c in openToClose.keys) {
            stack.add(c)
        } else {
            val expectedChar = stack.removeLastOrNull()
                ?.let { openToClose[it] }
            if (c != expectedChar) {
                return syntaxScores[c]
            }
        }
    }
    return null
}

fun scoreCompletion(line: String): Long {
    val stack = ArrayDeque<Char>()
    line.forEach { c ->
        if (c in openToClose.keys) {
            stack.add(c)
        } else {
            stack.removeLast()
        }
    }

    var score = 0L
    while (stack.isNotEmpty()) {
        score *= 5
        score += autocompleteScores[openToClose[stack.removeLast()]!!]!!
    }

    return score
}

fun main() = timed {
    var totalSyntaxScore = 0
    val completionScores = mutableListOf<Long>()

    (DATAPATH / "2021/day10.txt").useLines { lines ->
        lines.forEach { line ->
            val syntaxScore = findIllegalChar(line)
            if (syntaxScore != null) {
                totalSyntaxScore += syntaxScore
            } else {
                completionScores.add(scoreCompletion(line))
            }
        }
    }

    println("Part one: $totalSyntaxScore")
    completionScores.sorted()[completionScores.size / 2]
        .also { println("Part two: $it") }
}
