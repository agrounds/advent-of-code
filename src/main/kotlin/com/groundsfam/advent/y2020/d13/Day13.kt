package com.groundsfam.advent.y2020.d13

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

// find lowest positive solution to the equations
// x = i1 (mod n1)
// x = i2 (mod n2)
// ...
// where input is a list of (n1, i1) pairs
// assumes that (n1, n2, ...) are mutually coprime
// and 0 <= i1 < n1 etc.
fun chineseRemainder(input: List<Pair<Int, Int>>): Long {
    var t = 0L
    var p = 1L

    input.forEach { (n, i) ->
        while (t % n != i.toLong()) {
            t += p
        }
        p *= n
    }

    return t
}


fun main() {
    var depart = 0
    var busIdsUnparsed = emptyList<String>()
    (DATAPATH / "2020/day13.txt").useLines { lines ->
        lines.forEachIndexed { i, line ->
            when (i) {
                0 -> depart = line.toInt()
                1 -> busIdsUnparsed = line.split(",")
            }
        }
    }

    var minWait = Integer.MAX_VALUE
    var bestBusId = -1

    busIdsUnparsed.filterNot { it == "x" }.map { it.toInt() }.let { busIds ->
        busIds.forEach { busId ->
            val wait = busId - (depart % busId)
            if (wait < minWait) {
                minWait = wait
                bestBusId = busId
            }
        }
    }

    println("Part one: ${minWait * bestBusId}")

    // (busId, index) pairs
    val busIdsIndices = mutableListOf<Pair<Int, Int>>().apply {
        busIdsUnparsed.forEachIndexed { i, busId  ->
            if (busId != "x") busId.toInt().let { bid ->
                add(bid to (-i).mod(bid))
            }
        }
    }

    chineseRemainder(busIdsIndices).also { println("Part two: $it") }
}