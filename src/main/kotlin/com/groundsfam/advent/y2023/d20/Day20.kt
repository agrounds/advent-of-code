package com.groundsfam.advent.y2023.d20

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


sealed class Module {
    abstract fun receive(pulse: Boolean, from: String): Boolean?
    abstract fun state(): List<Boolean>
}
class FlipFlop : Module() {
    private var state: Boolean = false
    override fun receive(pulse: Boolean, from: String): Boolean? =
        if (pulse) {
            null
        } else {
            state = !state
            state
        }

    override fun state(): List<Boolean> = listOf(state)
}
class Conjunction(inputs: Set<String>) : Module() {
    private val recentPulses = inputs
        .associateWith { false }
        .toMutableMap()
    override fun receive(pulse: Boolean, from: String): Boolean {
        recentPulses[from] = pulse
        return recentPulses.any { (_, v) -> !v }
    }

    override fun state(): List<Boolean> = recentPulses.values.toList()
}
data object Broadcaster : Module() {
    override fun receive(pulse: Boolean, from: String): Boolean = pulse
    override fun state(): List<Boolean> = emptyList()
}

data class ModuleConnection(val module: Module, val destinations: List<String>)
data class RxInputPartition(
    val modules: Set<Module>,
    val states: MutableMap<Long, Int> = mutableMapOf(),
    var period: Int? = null,
    var repeatFrom: Int? = null,
    val lowPulses: MutableList<List<Int>>,
    val highPulses: MutableList<List<Int>>,
)

class Solution(private val connections: Map<String, ModuleConnection>) {
    private val rxInputPartitions: Map<String, RxInputPartition>
    private var buttonPresses: Int = 0

    init {
        // In my input, there is a single conjunction module that connects to rx (call it rxIn),
        // and four conjunction modules that connect to that one. The module graph seems to be
        // partitioned, so that these modules run totally independently of one another.
        // Therefore, our strategy is to identify loops in state for each of these partitions
        // separately, and the time at which these modules send high pulses to rxIn. rxIn sends
        // a low pulse to rx exactly when it receives a high pulse from each of its inputs, and this
        // must happen simultaneously.

        fun incoming(moduleName: String): Set<String> =
            connections
                .filterValues { it.destinations.contains(moduleName) }
                .keys

        val rxIn = incoming("rx").first()
        rxInputPartitions = connections
            .filterValues { it.destinations.contains(rxIn) }
            .keys
            .associateWith { moduleName ->
                val partition = mutableSetOf<Module>()
                val visited = mutableSetOf<String>()
                visited.addAll(listOf("broadcaster", rxIn))

                val queue = ArrayDeque<String>()
                queue.add(moduleName)

                while (queue.isNotEmpty()) {
                    val name = queue.removeFirst()
                    visited.add(name)
                    connections[name]?.module?.also {
                        partition.add(it)
                        (incoming(name) + connections[name]!!.destinations).forEach { n ->
                            if (n !in visited) {
                                queue.add(n)
                            }
                        }
                    }
                }
                RxInputPartition(
                    partition,
                    // add empty lists here to represent the fact that no pulses are sent
                    // before the button has been pressed the first time
                    lowPulses = mutableListOf(emptyList()),
                    highPulses = mutableListOf(emptyList())
                )
            }
    }

    fun pressButton(): Pair<Int, Int> {
        buttonPresses++

        data class Pulse(val pulse: Boolean, val from: String, val to: String)

        val pulseQueue = ArrayDeque<Pulse>()
        pulseQueue.add(Pulse(false, "button", "broadcaster"))
        var lowPulses = 0
        var highPulses = 0
        val rxInputLowPulses = rxInputPartitions.mapValues { mutableListOf<Int>() }
        val rxInputHighPulses = rxInputPartitions.mapValues { mutableListOf<Int>() }

        while (pulseQueue.isNotEmpty()) {
            val (pulse, from, to) = pulseQueue.removeFirst()
            if (pulse) {
                highPulses++
            } else {
                lowPulses++
            }
            val rxInputPulses = if (pulse) rxInputHighPulses else rxInputLowPulses
            rxInputPulses[from]?.add(lowPulses + highPulses)

            connections[to]?.let { (module, destinations) ->
                val nextPulse = module.receive(pulse, from)
                if (nextPulse != null) {
                    destinations.forEach { destination ->
                        pulseQueue.add(Pulse(nextPulse, to, destination))
                    }
                }
            }
        }

        rxInputPartitions.forEach { (modName, partition) ->
            val state = partition.modules.flatMap { it.state() }.fold(0L) { s, v ->
                s * 2 + (if (v) 1 else 0)
            }
            val prevButtonPresses = partition.states[state]
            if (prevButtonPresses == null) {
                partition.states[state] = buttonPresses
            } else {
                partition.repeatFrom = prevButtonPresses
                partition.period = buttonPresses - prevButtonPresses
            }
            partition.lowPulses.add(rxInputLowPulses[modName]!!)
            partition.highPulses.add(rxInputHighPulses[modName]!!)
        }

        return Pair(lowPulses, highPulses)
    }

    fun seekPartitionPeriods(): List<RxInputPartition> {
        while (rxInputPartitions.values.any { it.period == null }) {
            pressButton()
        }
        return rxInputPartitions.values.toList()
    }
}

fun main() = timed {
    val solution = (DATAPATH / "2023/day20.txt").useLines { lines ->
        val inputs = mutableMapOf<String, MutableSet<String>>()
        val outputs = mutableMapOf<String, List<String>>()
        val types = mutableMapOf<String, Char>()

        lines.forEach { line ->
            val (module, destinationString) = line.split(" -> ")
            val moduleName =
                if (module[0] == 'b') module // special case -- broadcaster doesnt have a symbol representing its type
                else module.substring(1)
            types[moduleName] = module[0]
            val destinations = destinationString.split(", ")

            destinations.forEach { destination ->
                val inputsSet = inputs[destination] ?: (
                    mutableSetOf<String>()
                        .also { inputs[destination] = it }
                    )
                inputsSet.add(moduleName)
            }
            outputs[moduleName] = destinations
        }

        types.map { (moduleName, t) ->
            val module = when (t) {
                '%' -> FlipFlop()
                '&' -> Conjunction(inputs[moduleName] ?: emptySet())
                'b' -> Broadcaster
                else -> throw RuntimeException("Invalid module type $t")
            }

            moduleName to ModuleConnection(module, outputs[moduleName]!!)
        }.toMap()
    }
        .let(::Solution)

    (0 until 1000)
        .fold(Pair(0L, 0L)) { (lowSum, highSum), i ->
            val (low, high) = solution.pressButton()
            Pair(lowSum + low, highSum + high)
        }
        .let { (l, h) -> l * h }
        .also { println("Part one: $it") }
    val res = solution.seekPartitionPeriods()
    println()
}
