package advent.y2015.d07

import advent.y2015.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

val MAX_SIGNAL = 0x10000.toUInt()

typealias Signal = UInt
infix fun Signal.shiftLeft(amount: Int) = shl(amount) % MAX_SIGNAL
infix fun Signal.shiftRight(amount: Int) = shr(amount)
fun Signal.not(): Signal = inv() % MAX_SIGNAL

sealed class Wiring

data class ConstWiring(val value: Signal): Wiring()
data class DirectWiring(val source: String): Wiring()
data class AndWiring(val source1: String, val source2: String): Wiring()
data class OrWiring(val source1: String, val source2: String): Wiring()
data class LShiftWiring(val source: String, val amount: Int): Wiring()
data class RShiftWiring(val source: String, val amount: Int): Wiring()
data class NotWiring(val source: String): Wiring()

fun parseWiring(line: String): Pair<String, Wiring> {
    val parts = line.split(" ")
    val wiring = when {
        parts[0].toIntOrNull() != null ->
            ConstWiring(parts[0].toUInt())
        parts[0] == "NOT" ->
            NotWiring(parts[1])
        parts[1] == "AND" ->
            AndWiring(parts[0], parts[2])
        parts[1] == "OR" ->
            OrWiring(parts[0], parts[2])
        parts[1] == "LSHIFT" ->
            LShiftWiring(parts[0], parts[2].toInt())
        parts[1] == "RSHIFT" ->
            RShiftWiring(parts[0], parts[2].toInt())
        else ->
            DirectWiring(parts[0])
    }
    return parts.last() to wiring
}

fun partOne(wiringMap: Map<String, Wiring>, wire: String): Signal {
    val signals: MutableMap<String, Signal> = mutableMapOf()

    fun resolveWire(wire: String): Signal {
        if (wire in signals) return signals[wire]!!
        val resolvedSignal: Signal = when (val wiring = wiringMap[wire]) {
            is ConstWiring ->
                wiring.value
            is DirectWiring ->
                resolveWire(wiring.source)
            is AndWiring ->
                resolveWire(wiring.source1) and resolveWire(wiring.source2)
            is OrWiring ->
                resolveWire(wiring.source1) or resolveWire(wiring.source2)
            is LShiftWiring ->
                resolveWire(wiring.source) shiftLeft wiring.amount
            is RShiftWiring ->
                resolveWire(wiring.source) shiftRight  wiring.amount
            is NotWiring ->
                resolveWire(wiring.source).not()
            null ->
                throw RuntimeException("Missing signal for wire $wire")
        }
        signals[wire] = resolvedSignal
        return resolvedSignal
    }
    return resolveWire(wire)
}


fun main() {
    val wiringMap: Map<String, Wiring> = (DATAPATH / "day07.txt").useLines { lines ->
        lines.toList().associate(::parseWiring)
    }
    partOne(wiringMap, "a")
        .also { println("Part one: $it") }
}
