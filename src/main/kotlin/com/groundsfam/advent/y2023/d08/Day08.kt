package com.groundsfam.advent.y2023.d08

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.lcm
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readLines

data class State(val node: String, val i: Int)
data class EndCondition(val solutions: Set<Int>, val modulo: Int, val minSolution: Int)

class Solution(private val directions: String, private val network: Map<String, Pair<String, String>>) {
    fun State.next() = State(
        nextNode(node, i.toLong()),
        (i + 1) % directions.length
    )
    private fun nextNode(node: String, i: Long): String =
        network[node]?.let { (l, r) ->
            if (directions[(i % directions.length).toInt()] == 'L') l
            else r
        } ?: throw RuntimeException("Node $node not found in network!")

    fun partOne(): Long {
        var node = "AAA"
        return generateSequence(0L) { it + 1 }.first { i ->
            node = nextNode(node, i)
            node == "ZZZ"
        } + 1
    }

    private fun findEndCondition(start: String): EndCondition {
        // track first end node found
        var firstEndSteps: Int? = null
        var stepsTaken = 0

        // r1 and r2 will intersect somewhere in the loop
        var r1 = State(start, 0).next()
        var r2 = State(start, 0).next().next()
        stepsTaken++
        if (r1.node.endsWith('Z')) {
            firstEndSteps = stepsTaken
        }
        while (r1 != r2) {
            r1 = r1.next()
            r2 = r2.next().next()
            stepsTaken++
            if (firstEndSteps == null && r1.node.endsWith('Z')) {
                firstEndSteps = stepsTaken
            }
        }

        // r1 and r2 will intersect at beginning of the loop
        // stepsToLoop is equal to length of path from start to beginning of loop
        var stepsToLoop = 0
        r2 = State(start, 0)
        while (r1 != r2) {
            r1 = r1.next()
            r2 = r2.next()
            stepsToLoop++
            stepsTaken++
            if (firstEndSteps == null && r1.node.endsWith('Z')) {
                firstEndSteps = stepsTaken
            }
        }
        val loopStart = r1

        // traverse through loop again to find its length
        // and find locations of end nodes
        val ends = mutableSetOf<Int>()
        var loopLength = 0

        do {
            if (r1.node.endsWith('Z')) {
                ends.add(loopLength)
            }
            r1 = r1.next()
            loopLength++

        } while (r1 != loopStart)

        return EndCondition(
            ends.mapTo(mutableSetOf()) { (it + stepsToLoop) % loopLength },
            loopLength,
            firstEndSteps ?: throw RuntimeException("No end node found!")
        )
    }

    fun partTwo(): Long {
        val conditions = network.keys
            .filter { it.endsWith('A') }
            .map(::findEndCondition)

        // observe all of them have single solution at exactly `modulo` steps...
        return conditions.map { it.modulo.toLong() }.lcm()
    }
}

fun main() = timed {
    val lines = (DATAPATH / "2023/day08.txt").readLines()
    val directions = lines.first()
    val network = mutableMapOf<String, Pair<String, String>>()
    lines.subList(2, lines.size).forEach { line ->
        network[line.substring(0..2)] = Pair(line.substring(7..9), line.substring(12..14))
    }
    val solution = Solution(directions, network)
    println("Part one: ${solution.partOne()}")
    println("Part two: ${solution.partTwo()}")
}
