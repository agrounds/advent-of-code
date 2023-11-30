package com.groundsfam.advent.y2020.d25

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines


const val INITIAL_SUBJECT = 7
const val BASE = 20201227

fun findLoop(publicKey: Int): Int {
    var value = 1
    var loop = 0
    while (value != publicKey) {
        value *= INITIAL_SUBJECT
        value %= BASE
        loop++
    }
    return loop
}

fun encrypt(subject: Int, loop: Int): Long =
    (0 until loop).fold(1L) { acc, _ ->
        (acc * subject) % BASE
    }

fun main() {
    val publicKeys = (DATAPATH / "2020/day25.txt").useLines { lines ->
        lines.toList().map { it.toInt() }
    }.also { assert(it.size == 2) }
    val loops = publicKeys.map(::findLoop)
    println("Part one: ${encrypt(publicKeys[0], loops[1])}")
}
