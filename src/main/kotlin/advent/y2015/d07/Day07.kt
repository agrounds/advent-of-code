package advent.y2015.d07

import advent.y2015.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

typealias Signal = Pair<UByte, UByte>
fun Int.toSignal(): Signal {
    if (this !in 0..65535) throw RuntimeException("Number is outside valid signal range: $this")
    return (this / 256).toUByte() to (this % 256).toUByte()
}

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
            ConstWiring(parts[0].toInt().toSignal())
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

fun partOne() {

}


fun main() {
    val wiringMap: Map<String, Wiring> = (DATAPATH / "day07.txt").useLines { lines ->
        lines.toList().associate(::parseWiring)
    }
}
