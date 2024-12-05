package com.groundsfam.advent.y2024.d05

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

class Solution(private val rules: Map<Int, MutableSet<Int>>) {
    fun isSorted(update: List<Int>): Boolean {
        update.forEachIndexed { i, a ->
            val afterA = rules[a]
            if (afterA != null) {
                (0 until i).forEach { j ->
                    if (update[j] in afterA) {
                        return false
                    }
                }
            }
        }
        return true
    }
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
    val solution = Solution(rules)
    updates
        .filter(solution::isSorted)
        .sumOf { it[it.size / 2] }
        .also { println("Part one: $it") }
}
