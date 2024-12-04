package com.groundsfam.advent.y2015.d16

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

data class Aunt(val id: Int, val properties: Map<String, Int>)

val propertiesToMatch = mapOf(
    "children" to 3,
    "cats" to 7,
    "samoyeds" to 2,
    "pomeranians" to 3,
    "akitas" to 0,
    "vizslas" to 0,
    "goldfish" to 5,
    "trees" to 3,
    "cars" to 2,
    "perfumes" to 1,
)

fun parseLine(line: String): Aunt {
    val parts = line.split("""[:,]?\s""".toRegex())
    val id = parts[1].toInt()
    val numProperties = (parts.size / 2) - 1

    val properties = (1..numProperties).mapTo(mutableSetOf()) { i ->
        parts[2 * i] to parts[2 * i + 1].toInt()
    }.toMap()

    return Aunt(id, properties)
}

fun auntMatches(aunt: Aunt, partTwo: Boolean): Boolean =
    aunt.properties.all { (k, v) ->
        when (k) {
            "cats", "trees" -> {
                if (partTwo) {
                    propertiesToMatch[k]!! < v
                } else {
                    propertiesToMatch[k] == v
                }
            }

            "pomeranians", "goldfish" -> {
                if (partTwo) {
                    propertiesToMatch[k]!! > v
                } else {
                    propertiesToMatch[k] == v
                }
            }

            else -> {
                propertiesToMatch[k] == v
            }
        }
    }

fun main() = timed {
    val aunts = (DATAPATH / "2015/day16.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { parseLine(it) }
    }
    aunts.firstOrNull { auntMatches(it, false) }
        .also { println("Part one: ${it?.id}") }
    aunts.firstOrNull { auntMatches(it, true) }
        .also { println("Part two: ${it?.id}") }
}
