package com.groundsfam.advent.y2020.d23

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

// Implements part of the API of a mutable doubly linked list
data class ListNode(var prev: ListNode?, var next: ListNode?, val value: Int) {
    override fun toString() = "ListNode(prev=${prev?.value}, (value=$value), next=${next?.value})"
}

class Solver(start: String, private val numCups: Int) {
    // map of value -> ListNode containing that value
    private val cups: Map<Int, ListNode>

    // initializes a circular doubly linked list of cups
    init {
        val startInts = start.map { it - '0' }
        val ret = mutableMapOf<Int, ListNode>()
        var prev = ListNode(null, null, startInts.first())
            .also { ret[it.value] = it }
        startInts.subList(1, startInts.size).forEach { value ->
            val curr = ListNode(prev, null, value)
            prev.next = curr
            ret[curr.value] = curr
            prev = curr
        }
        (10..numCups).forEach { value ->
            val curr = ListNode(prev, null, value)
            prev.next = curr
            ret[curr.value] = curr
            prev = curr
        }
        ret[startInts.first()]!!.let {
            prev.next = it
            it.prev = prev
        }
        cups = ret
    }

    private fun decrement(cup: Int): Int =
        (cup + numCups - 2) % numCups + 1

    // returns next current cup
    fun doMove(currentCup: Int): Int {
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
        var dest = decrement(currentCup)
        while (dest in taken.map { it.value }) dest = decrement(dest)
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

    fun getCup(cupValue: Int): ListNode? = cups[cupValue]
}


fun main() {
    val start = (DATAPATH / "2020/day23.txt").useLines { lines ->
        lines.first()
    }
    val solver1 = Solver(start, 9)
    var curr1 = start.first() - '0'
    repeat(100) {
        curr1 = solver1.doMove(curr1)
    }
    var cup = solver1.getCup(1)!!
    (0 until 8).map {
        cup = cup.next!!
        cup.value
    }.also { println("Part one: ${it.joinToString("")}") }

    val solver2 = Solver(start, 1_000_000)
    var curr2 = start.first() - '0'
    repeat(10_000_000) {
        curr2 = solver2.doMove(curr2)
    }
    solver2.getCup(1)!!.let {
        it.next!!.value.toLong() * it.next!!.next!!.value.toLong()
    }.also { println("Part two: $it") }
}
