package advent.y2020.d18

import advent.y2020.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

private fun evaluate(expression: String, from: Int? = null, to: Int? = null): Long {
    var curr = from ?: 0


    return 0
}

fun main() {
    val expressions = (DATAPATH / "day18.txt").useLines { it.toList() }

    expressions.sumOf { evaluate(it) }
        .also { println("Part one: $it") }
}