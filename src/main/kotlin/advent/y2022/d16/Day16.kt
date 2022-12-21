package advent.y2022.d16

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

data class Valve(val name: String, val flowRate: Int, val adjacentValves: List<String>)
fun parseLine(line: String): Valve {
    val parts = line.split(" ")
    val flowRate = parts[4].filter { it in '0'..'9' }.toInt()
    val adjacentValves = parts.subList(9, parts.size).map { it.take(2) }
    return Valve(parts[1], flowRate, adjacentValves)
}

data class State(val position: String, val openValves: Set<String>, val timeRemaining: Int)
fun State.moveTo(newPosition: String) = copy(position = newPosition, timeRemaining = timeRemaining - 1)
fun State.openValve() = copy(openValves = openValves + position, timeRemaining = timeRemaining - 1)
fun State.valveIsOpen() = position in openValves
val START = State("AA", emptySet(), 30)

fun maxFlow(valves: Map<String, Valve>): Int {
    fun State.flowRate() = openValves.sumOf { valves[it]!!.flowRate }

    var maxFlow = 0

    val states = mutableMapOf<State, Int>()
    val queue = ArrayDeque<State>()
    states[START] = 0
    queue.addFirst(START)

    while (queue.isNotEmpty()) {
        val currState = queue.removeFirst()
        val currFlow = states[currState]!!
        if (currState.timeRemaining == 0) {
            maxFlow = maxOf(maxFlow, states[currState]!!)
        } else {
            val newFlow = currFlow + currState.flowRate()
            if (!currState.valveIsOpen() && valves[currState.position]!!.flowRate > 0) {
                val opened = currState.openValve()
                if (opened in states) {
                    states[opened] = maxOf(states[opened]!!, newFlow)
                } else {
                    states[opened] = newFlow
                    queue.add(opened)
                }
            }
            valves[currState.position]!!.adjacentValves.forEach { valve ->
                val moved = currState.moveTo(valve)
                if (moved in states) {
                    states[moved] = maxOf(states[moved]!!, newFlow)
                } else {
                    states[moved] = newFlow
                    queue.add(moved)
                }
            }
        }
    }

    return maxFlow
}


fun main() {
    val valves = (DATAPATH / "2022/day16.txt").useLines { lines ->
        lines.toList().map(::parseLine)
            .associateBy { it.name }
    }
    println("Part one: ${maxFlow(valves)}")
}
