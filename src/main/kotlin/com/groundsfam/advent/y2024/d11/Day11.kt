package com.groundsfam.advent.y2024.d11

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.pow
import com.groundsfam.advent.timed
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.div
import kotlin.io.path.readText
import kotlin.io.path.writeLines
import kotlin.math.log10

fun changeStone(stone: Long): List<Long> {
    if (stone == 0L) return listOf(1)
    val numDigits = log10(stone.toDouble()).toInt() + 1
    if (numDigits % 2 == 0) {
        val pow10 = 10.pow(numDigits / 2)
        return listOf(stone / pow10, stone % pow10)
    }
    return listOf(2024 * stone)
}

fun blink(stoneCounts: Map<Long, Long>): Map<Long, Long> =
    stoneCounts.entries.fold(mutableMapOf()) { newStoneCounts, (stone, count) ->
        changeStone(stone).forEach { newStone ->
            newStoneCounts[newStone] =  (newStoneCounts[newStone] ?: 0) + count
        }
        newStoneCounts
    }

fun main() = timed {
    var stoneCounts: Map<Long, Long> = (DATAPATH / "2024/day11.txt").readText()
        .trim()
        .split(" ")
        .fold(mutableMapOf()) { counts, n ->
            val n1 = n.toLong()
            counts[n1] = (counts[n1] ?: 0) + 1
            counts
        }
    repeat(25) {
        stoneCounts = blink(stoneCounts)
    }
    println("Part one: ${stoneCounts.values.sum()}")
    repeat(50) {
        stoneCounts = blink(stoneCounts)
    }
    println("Part two: ${stoneCounts.values.sum()}")
}
