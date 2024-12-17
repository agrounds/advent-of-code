package com.groundsfam.advent.y2019.d06

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

const val YOU = "YOU"
const val SAN = "SAN"

fun countOrbits(directOrbits: List<Pair<String, String>>): Int {
    val groupedOrbits: Map<String, MutableSet<String>> = directOrbits.groupingBy { it.first }.aggregate { _, set, element, _ ->
        (set ?: mutableSetOf())
            .also { it.add(element.second) }
    }
    val counts = mutableMapOf<String, Int>()
    fun getOrbits(from: String): Int {
        counts[from]?.also { return it }

        return (groupedOrbits[from] ?: emptySet())
            .sumOf { getOrbits(it) + 1 }
            .also { counts[from] = it }
    }

    return groupedOrbits.keys.sumOf(::getOrbits)
}

fun findPath(directOrbits: List<Pair<String, String>>): Int {
    data class Path(val pos: String, val len: Int)

    val neighbors = mutableMapOf<String, MutableSet<String>>()
    directOrbits.forEach { (a, b) ->
        listOf(a to b, b to a).forEach { (a, b) ->
            val aNeighbors = neighbors[a] ?: mutableSetOf<String>().also { neighbors[a] = it }
            aNeighbors.add(b)
        }
    }

    val start = neighbors[YOU]!!.first()
    val goal = neighbors[SAN]!!.first()
    val queue = ArrayDeque<Path>()
    val seen = mutableSetOf<String>()
    queue.add(Path(start, 0))

    while (queue.isNotEmpty()) {
        val (pos, len) = queue.removeFirst()
        if (pos == goal) return len
        if (!seen.add(pos)) continue

        neighbors[pos]!!.forEach { next ->
            queue.add(Path(next, len + 1))
        }
    }

    throw RuntimeException("No path from YOU to SAN!")
}

fun main() = timed {
    val directOrbits = (DATAPATH / "2019/day06.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            line.substring(0..2) to line.substring(4..6)
        }
    }
    println("Part one: ${countOrbits(directOrbits)}")
    println("Part two: ${findPath(directOrbits)}")
}
