package advent.y2020.d23

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun decrement(cup: Int): Int = (cup + 7) % 9 + 1

fun doMove(cups: List<Int>): List<Int> {
    var next = decrement(cups.first())
    val taken = cups.subList(1, 4)
    while (next in taken) next = decrement(next)
    val nextIdx = cups.indexOf(next)
    return listOf(cups.first()) + cups.subList(4, nextIdx + 1) + taken + cups.subList(nextIdx + 1, cups.size)
}


fun main() {
    val start = (DATAPATH / "2020/day23.txt").useLines { lines ->
        lines.first().map { it - '0' }
    }
    var cups = start
    repeat(100) {
        cups = doMove(cups)
        cups = cups.subList(1, cups.size) + listOf(cups.first())
    }
    cups.indexOf(1).let { idx ->
        cups.subList(idx + 1, cups.size) + cups.subList(0, idx)
    }.also { println("Part one: ${it.joinToString("")}") }
}
