package com.groundsfam.advent.y2023.d20

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


sealed class Module {
    abstract fun receive(pulse: Boolean, from: String): Boolean?
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
}
class Conjunction(inputs: Set<String>) : Module() {
    private val recentPulses = inputs
        .associateWith { false }
        .toMutableMap()
    override fun receive(pulse: Boolean, from: String): Boolean {
        recentPulses[from] = pulse
        return recentPulses.any { (_, v) -> !v }
    }
}
data object Broadcaster : Module() {
    override fun receive(pulse: Boolean, from: String): Boolean = pulse
}

data class ModuleConnection(val module: Module, val destinations: List<String>)

data class ButtonResult(val lowPulses: Int, val highPulses: Int, val rxLowPulses: Int)
class Solution(private val connections: Map<String, ModuleConnection>) {
    private val rxInputPartitions: List<Set<Module>>

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
            .map { moduleName ->
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
                partition
            }
    }

    fun pressButton(): ButtonResult {
        data class Pulse(val pulse: Boolean, val from: String, val to: String)

        val pulseQueue = ArrayDeque<Pulse>()
        pulseQueue.add(Pulse(false, "button", "broadcaster"))
        var lowPulses = 0
        var highPulses = 0
        var rxLowPulses = 0

        while (pulseQueue.isNotEmpty()) {
            val (pulse, from, to) = pulseQueue.removeFirst()
            if (pulse) {
                highPulses++
            } else {
                lowPulses++
                if (to == "rx") {
                    rxLowPulses++
                }
            }

            connections[to]?.let { (module, destinations) ->
                val nextPulse = module.receive(pulse, from)
                if (nextPulse != null) {
                    destinations.forEach { destination ->
                        pulseQueue.add(Pulse(nextPulse, to, destination))
                    }
                }
            }
        }

        return ButtonResult(lowPulses, highPulses, rxLowPulses)
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

    var buttonPressesToStartSand: Int? = null
    val rxLowPulseCounts = mutableMapOf<Int, Int>()
    (0 until 1000)
        .fold(Pair(0L, 0L)) { (lowSum, highSum), i ->
            val (low, high, rxLowPresses) = solution.pressButton()
            rxLowPulseCounts[rxLowPresses] = (rxLowPulseCounts[rxLowPresses] ?: 0) + 1
            if (buttonPressesToStartSand == null && rxLowPresses == 1) {
                buttonPressesToStartSand = (i + 1)
            }
            Pair(lowSum + low, highSum + high)
        }
        .let { (l, h) -> l * h }
        .also { println("Part one: $it") }

    var buttonPresses = 1000
    while (buttonPressesToStartSand == null && buttonPresses < 1_000_000) {
        val (_, _, rxLowPresses) = solution.pressButton()
        rxLowPulseCounts[rxLowPresses] = (rxLowPulseCounts[rxLowPresses] ?: 0) + 1
        buttonPresses++
        if (rxLowPresses == 1) {
            buttonPressesToStartSand = buttonPresses
        }
    }
    println("Part two: $buttonPressesToStartSand")
}
