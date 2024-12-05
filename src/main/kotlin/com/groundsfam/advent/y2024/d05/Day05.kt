package com.groundsfam.advent.y2024.d05

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

fun sort(update: List<Int>, rules: Map<Int, Set<Int>>): List<Int> {
    val ret = update.toMutableList()

    ret.indices.forEach { i ->
        (i + 1 until ret.size).forEach { j ->
            if (rules[ret[j]]?.let { ret[i] in it } == true) {
                val tmp = ret[i]
                ret[i] = ret[j]
                ret[j] = tmp
            }
        }
    }

    return ret
}

fun main() = timed {
    val rules = mutableMapOf<Int, MutableSet<Int>>()
    val updates = mutableListOf<List<Int>>()
    (DATAPATH / "2024/day05.txt").useLines { lines ->
        var isRule = true
        lines.forEach { line ->
            when {
                line.isBlank() -> {
                    isRule = false
                }

                isRule -> {
                    val (a, b) = line.split("|").map(String::toInt)
                    val set = rules[a] ?: (mutableSetOf<Int>().also { rules[a] = it })
                    set.add(b)
                }

                else -> {
                    updates.add(line.split(",").map(String::toInt))
                }
            }
        }
    }

    var sortedSum = 0
    var unsortedSum = 0
    updates.forEach { update ->
        val sorted = sort(update, rules)
        val mid = sorted[sorted.size / 2]
        if (update == sorted) {
            sortedSum += mid
        } else {
            unsortedSum += mid
        }
    }
    println("Part one: $sortedSum")
    println("Part one: $unsortedSum")
}
