package com.groundsfam.advent.y2022.d20

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

const val KEY = 811589153

data class Node(val num: Int, var prev: Node?, var next: Node?) {
    override fun toString() = "Node(num=$num, prev=${prev?.num}, next=${next?.num})"
}
// creates a new circular doubly linked list of the given values and returns an
// array to allow random access to nodes of the linked list, given the original index
// in the `sums` list
fun toIndexedLinkedList(nums: List<Int>): Array<Node> {
    val ret = mutableListOf<Node>()
    var prev: Node? = null
    nums.forEach { num ->
        val node = Node(num, prev, null)
        ret.add(node)
        prev?.let {
            it.next = node
        }
        prev = node
    }

    val firstNode = ret.first()
    val lastNode = ret.last()
    firstNode.prev = lastNode
    lastNode.next = firstNode

    return Array(nums.size) { ret[it] }
}

fun toList(linkedList: Array<Node>, zeroIdx: Int): List<Int> = mutableListOf<Int>().apply {
    add(0)
    var node = linkedList[zeroIdx].next!!
    while (node.num != 0) {
        add(node.num)
        node = node.next!!
    }
}


fun mix(nums: List<Int>, times: Int, key: Int): List<Int> {
    val linkedList = toIndexedLinkedList(nums)
    var zeroIdx: Int? = null
    repeat(times) {
        linkedList.forEachIndexed { i, node ->
            if (node.num == 0) zeroIdx = i

            var delta = (node.num.toLong() * key).mod(nums.size - 1)
            val forward = delta < nums.size / 2 - 1
            delta = if (forward) delta else nums.size - 1 - delta

            // remove node at its old position
            node.prev!!.next = node.next
            node.next!!.prev = node.prev
            // traverse to new position
            var newPrev = node.prev!!
            repeat(delta) {
                newPrev = if (forward) newPrev.next!! else newPrev.prev!!
            }
            // insert node
            node.prev = newPrev
            node.next = newPrev.next
            node.prev!!.next = node
            node.next!!.prev = node
        }
    }
    return toList(linkedList, zeroIdx!!)
}

fun main() = timed {
    val nums = (DATAPATH / "2022/day20.txt")
        .useLines { it.toList() }
        .map { it.toInt() }

    val mixed = mix(nums, 1, 1)  // zero is at index 0 of this new list
    (1000..3000 step 1000).sumOf {
        mixed[it % mixed.size]
    }.also { println("Part one: $it") }

    val mixed2 = mix(nums, 10, KEY)
    (1000..3000 step 1000).sumOf {
        mixed2[it % mixed2.size].toLong() * KEY
    }.also { println("Part two: $it") }
}
