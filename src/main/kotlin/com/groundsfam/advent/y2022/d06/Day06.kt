package com.groundsfam.advent.y2022.d06

import com.groundsfam.advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun seekPacketStart(buffer: String, distinctLength: Int): Int? {
    var i = 0
    while (i + distinctLength <= buffer.length) {
        if (buffer.substring(i, i + distinctLength).toSet().size == distinctLength) {
            return i + distinctLength
        }
        i++
    }
    return null
}



fun main() {
    (DATAPATH / "2022/day06.txt").useLines { lines ->
        lines.first()
    }
        .also { println("Part one: ${seekPacketStart(it, 4)}") }
        .also { println("Part two: ${seekPacketStart(it, 14)}") }
}
