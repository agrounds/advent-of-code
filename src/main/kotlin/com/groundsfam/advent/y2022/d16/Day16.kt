package com.groundsfam.advent.y2022.d16

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

data class Valve(val name: String, val flowRate: Int, val adjacentValves: List<String>)
fun parseLine(line: String): Valve {
    val parts = line.split(" ")
    val flowRate = parts[4].filter { it in '0'..'9' }.toInt()
    val adjacentValves = parts.subList(9, parts.size).map { it.take(2) }
    return Valve(parts[1], flowRate, adjacentValves)
}

const val START = "AA"

class Solver(valves: List<Valve>, private val time: Int) {
    private val nameToValve = valves.associateBy { it.name }
    private val nameToNumber = mutableMapOf<String, Int>()
    private val numberToValve: Array<Valve>
    private val distanceFromStart: IntArray
    private val distances: Array<IntArray>

    // The keys are bitmaps representing which valves are open.
    // E.g. 0...01101 means that valves 0, 2, 3 are open, others closed.
    // The value is the maximum amount of flow that can be released via some path
    // that eventually opens this set of valves, in some order.
    // Assumes that at most 32 valves can be opened!
    private val maxFlows = mutableMapOf<Int, Int>()

    init {
        // number the valves with nonzero flow rate
        valves.forEach { valve ->
            if (valve.flowRate > 0) {
                nameToNumber[valve.name] = nameToNumber.size
            }
        }
        numberToValve = Array(nameToNumber.size) { Valve("dummy", 0, emptyList()) }.apply {
            nameToNumber.forEach { (name, num) -> this[num] = nameToValve[name]!! }
        }

        // initialize distances
        distanceFromStart = computeDistances(nameToValve[START]!!)
        distances = Array(nameToNumber.size) {
            computeDistances(numberToValve[it])
        }

        computeMaxFlows()
    }

    private fun computeDistances(start: Valve): IntArray {
        val ret = IntArray(nameToNumber.size) { -1 }
        val queue = ArrayDeque(listOf(start to 0))
        val visited = mutableSetOf<String>()
        while (queue.isNotEmpty()) {
            val (valve, dist) = queue.removeFirst()
            visited.add(valve.name)
            nameToNumber[valve.name]?.let { n ->
                ret[n] =
                    if (ret[n] == -1) dist
                    else minOf(ret[n], dist)
            }
            valve.adjacentValves
                .filterNot { it in visited }
                .forEach {
                    queue.add(nameToValve[it]!! to (dist + 1))
                }
        }
        return ret
    }

    private fun totalFlowRate(openValves: Int): Int =
        numberToValve.mapIndexed { n, valve ->
            if (openValves and (1 shl n) != 0) valve.flowRate
            else 0
        }.sum()

    private fun computeMaxFlows() {
        // openValves is a bitmap, same as maxFlows' keys
        data class State(val position: Int, val openValves: Int, val timeRemaining: Int, val flowSoFar: Int) {
            val openValvesList = distances.indices.filter { openValves and (1 shl it) != 0 }
            override fun toString() = "State(position=$position, openValves=$openValvesList, timeRemaining=$timeRemaining, flowSoFar=$flowSoFar)"
        }
        fun State.goTo(newPosition: Int): State {
            val actionTime = distances[position][newPosition] + 1
            val newOpenValves = openValves or (1 shl newPosition)
            return State(newPosition, newOpenValves,
                timeRemaining - actionTime,
                flowSoFar + totalFlowRate(openValves) * actionTime)
        }

        val queue = distanceFromStart
            .mapIndexed { n, dist ->
                State(n, 1 shl n, time - (dist + 1), 0).takeIf { it.timeRemaining >= 0 }
            }
            .filterNotNull()
            .let(::ArrayDeque)

        while (queue.isNotEmpty()) {
            val curr = queue.removeFirst()
            maxFlows[curr.openValves] = maxOf(
                maxFlows[curr.openValves] ?: 0,
                curr.flowSoFar + totalFlowRate(curr.openValves) * curr.timeRemaining
            )
            distances.indices
                .filter { curr.openValves and (1 shl it) == 0 }  // do not go to an open valve
                .filter { distances[curr.position][it] < curr.timeRemaining - 1 }  // do not go to a valve we don't have time to open
                .forEach { newPosition ->
                    queue.add(curr.goTo(newPosition))
                }
        }
    }

    fun findMaxFlow(partOne: Boolean): Int =
        if (partOne)  maxFlows.values.maxOrNull()!!
        else maxFlows.maxOf { (openValves, flow) ->
            flow + (maxFlows.filterKeys { it and openValves == 0 }.values.maxOrNull() ?: 0)
        }
}


fun main() = timed {
    val valves = (DATAPATH / "2022/day16.txt").useLines { lines ->
        lines.toList()
            .map(::parseLine)
    }
    Solver(valves, 30)
        .also { println("Part one: ${it.findMaxFlow(true)}") }
    Solver(valves, 26)
        .also { println("Part two: ${it.findMaxFlow(false)}") }
}
