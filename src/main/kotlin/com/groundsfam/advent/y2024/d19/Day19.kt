package com.groundsfam.advent.y2024.d19

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

class Solution(private val towels: List<String>) {
    private val numWaysToMakeDp = mutableMapOf("" to 1L)
    fun numWaysToMake(design: String): Long {
        numWaysToMakeDp[design]?.also { return it }

        return towels.sumOf { t ->
            if (design.startsWith(t)) numWaysToMake(design.substring(t.length))
            else 0
        }.also {
            numWaysToMakeDp[design] = it
        }
    }
}

fun main() = timed {
    val (towels, designs) = (DATAPATH / "2024/day19.txt").useLines { lines ->
        val iter = lines.iterator()
        val towels = iter.next().split(", ")
        iter.next()
        val designs = mutableListOf<String>()
        iter.forEachRemaining(designs::add)

        Pair(towels, designs)
    }
    val s = Solution(towels)
    var canMake = 0
    var waysToMake: Long = 0
    designs.forEach {
        val ways = s.numWaysToMake(it)
        if (ways > 0) canMake++
        waysToMake += ways
    }

    println("Part one: $canMake")
    println("Part two: $waysToMake")
}
