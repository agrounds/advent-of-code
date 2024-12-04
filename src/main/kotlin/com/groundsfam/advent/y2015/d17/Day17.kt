package com.groundsfam.advent.y2015.d17

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

const val TOTAL = 150

fun countCombinations(capacities: List<Int>, target: Int, minContainers: Boolean): Int {
    // map from number of containers used
    // to number of combinations found for
    // that many user containers
    val counts = mutableMapOf<Int, Int>()

    // only using capacities in (start..end), how many ways are there
    // to fit the target amount?
    fun helper(start: Int, target: Int, usedContainers: Int) {
        if (target < 0) return
        if (target == 0) {
            counts[usedContainers] = counts.getOrDefault(usedContainers, 0) + 1
            return
        }
        if (start >= capacities.size) return

        helper(start + 1, target - capacities[start], usedContainers + 1)
        helper(start + 1, target, usedContainers)
    }
    helper(0, target, 0)

    return if (minContainers) {
        counts[counts.keys.minOrNull()!!]!!
    } else {
        counts.values.sum()
    }
}

fun main() = timed {
    val capacities = (DATAPATH / "2015/day17.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { it.toInt() }
    }
    println("Part one: ${countCombinations(capacities, TOTAL, false)}")
    println("Part two: ${countCombinations(capacities, TOTAL, true)}")
}
