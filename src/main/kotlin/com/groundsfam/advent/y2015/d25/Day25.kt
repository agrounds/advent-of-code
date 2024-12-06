package com.groundsfam.advent.y2015.d25

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readText

// this is a prime, so Z/pZ is a field
const val P = 33554393L
// this has an order of (p-1)/2 in Z/pZ
// we'll use this fact in our solution below
const val N = 252533L
// the code in position (1, 1)
const val START = 20151125L

fun getCode(r: Long, c: Long): Long {
    val diagNum = r + c - 1
    // there are (diagNum-1)-trangle-number codes before the codes in this diagonal
    // (the triangle numbers are 1, 3, 6, 10, 15, ... the nth one is equal to n(n+1)/2)
    // add c to get to the correct spot along the diagonal
    // subtract 1 since we don't do an operation to get the code at (1, 1)
    val numOperations = diagNum * (diagNum - 1) / 2 + c - 1
    // we can take the remainder mod (p-1)/2 because that is the order of N in the field Z/pZ
    return (0 until (numOperations % ((P - 1) / 2))).fold(START) { code, _ ->
        (code * N) % P
    }
}

fun main() = timed {
    val (r, c) = (DATAPATH / "2015/day25.txt").readText().let { line ->
        val parts = line.split(" ")
        parts[parts.size - 3].substringBefore(',').toLong() to
            parts[parts.size - 1].substringBefore('.').toLong()
    }
    println("Part one: ${getCode(r, c)}")
}
