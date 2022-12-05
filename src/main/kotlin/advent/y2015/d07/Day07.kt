package advent.y2015.d07

import advent.y2015.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

val MAX_SIGNAL = 0x10000.toUInt()

typealias Signal = UInt
infix fun Signal.shiftLeft(amount: Int) = shl(amount) % MAX_SIGNAL
infix fun Signal.shiftRight(amount: Int) = shr(amount)
fun Signal.not(): Signal = inv() % MAX_SIGNAL

sealed class Source
data class ConstantSource(val value: Signal): Source()
data class WireSource(val value: String): Source()
fun String.toSource(): Source =
    toUIntOrNull()?.let { ConstantSource(it) } ?: WireSource(this)

sealed class Wiring

data class DirectWiring(val source: Source): Wiring()
data class AndWiring(val source1: Source, val source2: Source): Wiring()
data class OrWiring(val source1: Source, val source2: Source): Wiring()
data class LShiftWiring(val source: Source, val amount: Int): Wiring()
data class RShiftWiring(val source: Source, val amount: Int): Wiring()
data class NotWiring(val source: Source): Wiring()

fun parseWiring(line: String): Pair<String, Wiring> {
    val parts = line.split(" ")

    val wiring = when (parts.size) {
        3 -> DirectWiring(parts[0].toSource())
        4 -> NotWiring(parts[1].toSource())
        5 ->
            when (parts[1]) {
                "AND" -> AndWiring(parts[0].toSource(), parts[2].toSource())
                "OR" -> OrWiring(parts[0].toSource(), parts[2].toSource())
                "LSHIFT" -> LShiftWiring(parts[0].toSource(), parts[2].toInt())
                "RSHIFT" -> RShiftWiring(parts[0].toSource(), parts[2].toInt())
                else -> throw RuntimeException("Invalid wiring: $line")
            }
        else -> throw RuntimeException("Invalid wiring: $line")
    }
    return parts.last() to wiring
}

fun resolveWireA(wiringMap: Map<String, Wiring>, initialSignals: Map<String, Signal>): Signal {
    val signals: MutableMap<String, Signal> = initialSignals.toMutableMap()

    fun resolveSignal(source: Source): Signal = when (source) {
        is ConstantSource -> source.value
        is WireSource -> {
            val wire = source.value
            if (source.value in signals) signals[wire]!!
            else {
                val resolvedSignal: Signal = when (val wiring = wiringMap[wire]) {
                    is DirectWiring ->
                        resolveSignal(wiring.source)
                    is AndWiring ->
                        resolveSignal(wiring.source1) and resolveSignal(wiring.source2)
                    is OrWiring ->
                        resolveSignal(wiring.source1) or resolveSignal(wiring.source2)
                    is LShiftWiring ->
                        resolveSignal(wiring.source) shiftLeft wiring.amount
                    is RShiftWiring ->
                        resolveSignal(wiring.source) shiftRight wiring.amount
                    is NotWiring ->
                        resolveSignal(wiring.source).not()
                    null ->
                        throw RuntimeException("Missing signal for wire $wire")
                }
                signals[wire] = resolvedSignal
                resolvedSignal
            }
        }
    }
    return resolveSignal(WireSource("a"))
}


fun main() {
    val wiringMap: Map<String, Wiring> = (DATAPATH / "day07.txt").useLines { lines ->
        lines.toList().associate(::parseWiring)
    }
    resolveWireA(wiringMap, emptyMap())
        .also { println("Part one: $it") }
    resolveWireA(wiringMap, mapOf("b" to 16076.toUInt()))
        .also { println("Part two: $it") }
}
