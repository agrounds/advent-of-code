package com.groundsfam.advent.y2016.d06

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readLines

fun recoverMessage(messages: List<String>, mostCommon: Boolean): String {
    val counts = Array<MutableMap<Char, Int>>(messages.first().length) { mutableMapOf() }
    messages.forEach { message ->
        message.forEachIndexed { i, c ->
            counts[i][c] = (counts[i][c] ?: 0) + 1
        }
    }
    return CharArray(counts.size) { i ->
        if (mostCommon) counts[i].maxBy { it.value }.key
        else counts[i].minBy { it.value }.key
    }.let(::String)
}

fun main() = timed {
    val messages = (DATAPATH / "2016/day06-example.txt").readLines()
    println("Part one: ${recoverMessage(messages, true)}")
    println("Part two: ${recoverMessage(messages, false)}")
}
