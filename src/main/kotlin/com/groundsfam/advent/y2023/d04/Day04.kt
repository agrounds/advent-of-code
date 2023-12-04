package com.groundsfam.advent.y2023.d04

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.pow
import kotlin.io.path.div
import kotlin.io.path.useLines


fun main() {
    val cards = (DATAPATH / "2023/day04.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            val (winners, yourNumbers) = line.split("""\s+\|\s+""".toRegex(), limit = 2)
            Pair(
                winners.split("""\s+""".toRegex())
                    .drop(2)  // Drop the "Game #:" part
                    .mapTo(mutableSetOf(), String::toInt),
                yourNumbers.split("""\s+""".toRegex())
                    .map(String::toInt)
            )
        }
    }
    cards.sumOf { (winners, yourNumbers) ->
        when (val c = yourNumbers.count { it in winners }) {
            0 -> 0
            else -> 2.pow(c - 1)
        }
    }
        .also { println("Part one: $it") }
}
