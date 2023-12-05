package com.groundsfam.advent.y2023.d02

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Tuple3
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

// tuple order: (red, green, blue)
fun parseGame(line: String): List<Tuple3<Int, Int, Int>> {
    val (_, draws) = line.split(": ", limit = 2)
    return draws.split("; ").map { draw ->
        val colorCounts = draw.split(", ").associate {
            val parts = it.split(" ")
            parts[1] to parts[0].toInt()
        }
        Tuple3(colorCounts["red"] ?: 0, colorCounts["green"] ?: 0, colorCounts["blue"] ?: 0)
    }
}

fun main() = timed {
    val games = (DATAPATH / "2023/day02.txt").useLines { lines ->
        lines.toList().map(::parseGame)
    }

    games.mapIndexed { idx, game ->
        // a game is possible if the max number of green dice observed was not more than 12
        // and similarly for the other colors
        if (game.all { it._1 <= 12 && it._2 <= 13 && it._3 <= 14 }) {
            idx + 1
        } else {
            0
        }
    }
        .sum()
        .also { println("Part one: $it") }

    games.sumOf { game ->
        // the minimum number of red dice making the game possible is the max of all observed red values
        // and the same goes for the other colors
        val minRed = game.maxOf { it._1 }
        val minGreen = game.maxOf { it._2 }
        val minBlue = game.maxOf { it._3 }
        minRed * minGreen * minBlue
    }
        .also { println("Part two: $it") }
}
