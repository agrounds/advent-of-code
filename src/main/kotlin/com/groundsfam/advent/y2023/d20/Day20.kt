package com.groundsfam.advent.y2023.d20

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


sealed class Module {
    /**
     * Determines what signal (high or low) to send to this module's
     * destinations, determined from the [pulse] received, and the
     * module that sent the signal to this module.
     *
     * [from] - Name of the module sending the pulse
     */
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

// In my input, there is a single conjunction module that connects to rx (call it rxIn),
// and four conjunction modules that connect to that one. The module graph seems to be
// partitioned, so that these modules run totally independently of one another.
// Therefore, my strategy is to identify loops in state for each of these partitions
// separately, and the time at which these modules send high pulses to rxIn. rxIn sends
// a low pulse to rx exactly when it receives a high pulse from each of its inputs, and this
// must happen simultaneously.
//
// Further examining my input, I found that high pulses on the rx input modules occur rarely.
// For each button press, there is at most one high pulse on an rx input, followed by low pulses.
// In the case that all four rx inputs send high pulses, they all do this before any of them
// sends low pulses. Thus, the problem reduces to finding the number of button presses in which
// all of them send high pulses, and their high pulses are guaranteed to produce a low pulse
// to rx.
//
// Examining the input further still, I found that the period by which each partition's state
// repeats is equal to the number of button presses that that partition first sent a high pulse
// on its rx input. This makes the modular arithmetic very easy: The puzzle solution is just the
// least common multiple of the number of button presses to get the first high pulse on each
// rx input. Furthermore, these numbers are pairwise coprime, so their least common multiple
// is just their product. The code below uses these simplifying assumptions about the input
// to simplify the code.

class Solution(private val connections: Map<String, ModuleConnection>) {
    private val rxInputHighPulse: MutableMap<String, Int?> = mutableMapOf()
    private var buttonPresses: Int = 0

    init {
        fun incoming(moduleName: String): Set<String> =
            connections
                .filterValues { it.destinations.contains(moduleName) }
                .keys

        incoming("rx").first()
            .let(::incoming)
            .forEach { rxInputHighPulse[it] = null }
    }

    fun pressButton(): Pair<Int, Int> {
        buttonPresses++

        data class Pulse(val pulse: Boolean, val from: String, val to: String)

        val pulseQueue = ArrayDeque<Pulse>()
        pulseQueue.add(Pulse(false, "button", "broadcaster"))
        var lowPulses = 0
        var highPulses = 0

        while (pulseQueue.isNotEmpty()) {
            val (pulse, from, to) = pulseQueue.removeFirst()
            if (pulse) {
                highPulses++
                if (from in rxInputHighPulse) {
                    rxInputHighPulse[from] = buttonPresses
                }
            } else {
                lowPulses++
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

        return Pair(lowPulses, highPulses)
    }

    fun findRxLowPulse(): Long {
        while (rxInputHighPulse.values.any { it == null }) {
            pressButton()
        }
        return rxInputHighPulse.values
            .fold(1L) { p, buttonPresses ->
                p * buttonPresses!!
            }
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

        types
            .map { (moduleName, t) ->
                val module = when (t) {
                    '%' -> FlipFlop()
                    '&' -> Conjunction(inputs[moduleName] ?: emptySet())
                    'b' -> Broadcaster
                    else -> throw RuntimeException("Invalid module type $t")
                }

                moduleName to ModuleConnection(module, outputs[moduleName]!!)
            }
            .toMap()
            .let(::Solution)
    }

    (0 until 1000)
        .fold(Pair(0L, 0L)) { (lowSum, highSum), _ ->
            val (low, high) = solution.pressButton()
            Pair(lowSum + low, highSum + high)
        }
        .let { (l, h) -> l * h }
        .also { println("Part one: $it") }

    println("Part two: ${solution.findRxLowPulse()}")
}
