package com.groundsfam.advent.y2021.d08

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


fun segmentsToDigit(segments: Set<Char>) = when (segments) {
    "abcefg".toSet() -> 0
    "cf".toSet() -> 1
    "acdeg".toSet() -> 2
    "acdfg".toSet() -> 3
    "bcdf".toSet() -> 4
    "abdfg".toSet() -> 5
    "abdefg".toSet() -> 6
    "acf".toSet() -> 7
    "abcdefg".toSet() -> 8
    "abcdfg".toSet() -> 9
    else -> throw RuntimeException("Invalid segments $segments")
}

data class Note(val signalPatterns: Set<Set<Char>>, val outputValue: List<String>) {
    // signalMap sends the segment in this note to the standard segment name for the same physical segment
    private val segmentMap: Map<Char, Char>

    init {
        val map = mutableMapOf<Char, Char>()

        val counts = "abcdefg".groupBy { c ->
            signalPatterns.count { c in it }
        }
        // these segments appear a unique number of times across all digits
        map[counts[6]!!.first()] = 'b'
        map[counts[4]!!.first()] = 'e'
        map[counts[9]!!.first()] = 'f'

        // segment c appears in the digit 1, along with f
        signalPatterns
            .first { it.size == 2 }
            .first { it !in map.keys }
            .also { map[it] = 'c' }

        // segments a and c appear in eight digits
        counts[8]!!
            .first { it !in map.keys }
            .also { map[it] = 'a' }

        // segment d appears in the digit 4, g does not
        signalPatterns
            .first { it.size == 4 }
            .first { it !in map.keys }
            .also { map[it] = 'd' }

        // segments d and g appear in seven digits
        counts[7]!!
            .first { it !in map.keys }
            .also { map[it] = 'g' }

        segmentMap = map
    }

    val decodedOutput: Int get() =
        outputValue
            .map { codedSegments ->
                codedSegments.mapTo(mutableSetOf()) { c -> segmentMap[c]!! }
            }
            .fold(0) { n, decodedSegments ->
                n * 10 + segmentsToDigit(decodedSegments)
            }
}

fun main() = timed {
    val notes: List<Note> = (DATAPATH / "2021/day08.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            line.split(" | ").let { (first, second) ->
                val signalPatterns = first
                    .split(" ")
                    .mapTo(mutableSetOf()) {
                        it.toSet()
                    }
                Note(signalPatterns, second.split(" "))
            }
        }
    }
    notes
        .sumOf { note ->
            note.outputValue.count { it.length in setOf(2, 3, 4, 7) }
        }
        .also { println("Part one: $it") }
    notes
        .sumOf { it.decodedOutput }
        .also { println("Part two: $it") }

}
