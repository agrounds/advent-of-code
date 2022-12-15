package advent.y2020.d23

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

// Implements part of the API of a mutable doubly linked list
data class ListNode(var prev: ListNode?, var next: ListNode?, val value: Int) {
    override fun toString() = "ListNode(prev=${prev?.value}, (value=$value), next=${next?.value})"
}

fun decrement(cup: Int, numCups: Int): Int = (cup + numCups - 2) % numCups + 1

fun doMove(cups: List<Int>): List<Int> {
    var next = decrement(cups.first(), 9)
    val taken = cups.subList(1, 4)
    while (next in taken) next = decrement(next, 9)
    val nextIdx = cups.indexOf(next)
    return listOf(cups.first()) + cups.subList(4, nextIdx + 1) + taken + cups.subList(nextIdx + 1, cups.size)
}

// Initializes a circular doubly linked list of cups.
// Returns map of value -> ListNode containing that value.
fun initCupList(start: List<Int>): Map<Int, ListNode> {
    val ret = mutableMapOf<Int, ListNode>()
    var prev = ListNode(null, null, start.first())
        .also { ret[it.value] = it }
    start.subList(1, start.size).forEach { value ->
        val curr = ListNode(prev, null, value)
        prev.next = curr
        ret[curr.value] = curr
        prev = curr
    }
    (10..1_000_000).forEach { value ->
        val curr = ListNode(prev, null, value)
        prev.next = curr
        ret[curr.value] = curr
        prev = curr
    }
    ret[start.first()]!!.let {
        prev.next = it
        it.prev = prev
    }
    return ret
}

// Returns next current cup
fun doMove(cups: Map<Int, ListNode>, currentCup: Int): Int {
    val curr = cups[currentCup]!!
    val taken = listOf(
        curr.next!!,
        curr.next!!.next!!,
        curr.next!!.next!!.next!!,
    )
    taken.last().next!!.let {
        curr.next = it
        it.prev = curr
    }
    var dest = decrement(currentCup, 1_000_000)
    while (dest in taken.map { it.value }) dest = decrement(dest, 1_000_000)
    val destBegin = cups[dest]!!
    val destEnd = destBegin.next!!
    taken.first().let {
        it.prev = destBegin
        destBegin.next = it
    }
    taken.last().let {
        it.next = destEnd
        destEnd.prev = it
    }
    return curr.next!!.value
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

    val cups2 = initCupList(start)
    var curr = start.first()
    repeat(10_000_000) {
        curr = doMove(cups2, curr)
    }
    cups2[1]!!.let {
        it.next!!.value.toLong() * it.next!!.next!!.value.toLong()
    }.also { println("Part two: $it") }
}
