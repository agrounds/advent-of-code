package com.groundsfam.advent.y2023.d04

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.pow
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.min

data class Card(val winners: Set<Int>, val yourNumbers: List<Int>)

fun Card.numMatches() = yourNumbers.count { it in winners }

fun partOne(cards: List<Card>) = cards.sumOf {
    when (val matches = it.numMatches()) {
        0 -> 0
        else -> 2.pow(matches - 1).toInt()
    }
}

fun partTwo(cards: List<Card>): Long {
    // initially we have one of each card
    val cardCounts = cards.mapTo(mutableListOf<Long>()) { 1 }
    cards.forEachIndexed { i, card ->
        val matches = card.numMatches()
        // indices of cards that get copied, making sure to stop at the end of the table of cards
        (i + 1..min(i + matches, cards.size - 1)).forEach { j ->
            // example: if we have 5 of card i, then card j gets 5 more copies
            cardCounts[j] += cardCounts[i]
        }
    }
    return cardCounts.sum()
}


fun main() = timed {
    (DATAPATH / "2023/day04.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            val (winners, yourNumbers) = line.split("""\s+\|\s+""".toRegex(), limit = 2)
            Card(
                winners.split("""\s+""".toRegex())
                    .drop(2)  // Drop the "Game #:" part
                    .mapTo(mutableSetOf(), String::toInt),
                yourNumbers.split("""\s+""".toRegex())
                    .map(String::toInt)
            )
        }
    }
        .also { println("Part one: ${partOne(it)}") }
        .also { println("Part two: ${partTwo(it)}") }
}
