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

fun main() = timed {
    val connections: Map<String, ModuleConnection> = (DATAPATH / "2023/day20.txt").useLines { lines ->
        val inputs = mutableMapOf<String, MutableSet<String>>()
        val outputs = mutableMapOf<String, List<String>>()
        val types = mutableMapOf<String, Char>()

        lines.forEach { line ->
            val (module, destinationString) = line.split(" -> ")
            val moduleName = module.substring(1)
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
}
