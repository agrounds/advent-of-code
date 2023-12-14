package com.groundsfam.advent.y2021.d12

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


private const val START = "start"
private const val END = "end"

private data class Path(val currCave: String, val visitedCaves: Set<String>)

private fun findPaths(map: Map<String, Set<String>>): Int {
    val queue = ArrayDeque<Path>()
    queue.add(Path(START, setOf(START)))
    var count = 0

    while (queue.isNotEmpty()) {
        val (currCave, visitedCaves) = queue.removeFirst()
        map[currCave]!!
            .filterNot { it in visitedCaves }
            .forEach { nextCave ->
                if (nextCave == END) {
                    count++
                } else {
                    val nextVisited =
                        if (nextCave[0].isUpperCase()) visitedCaves
                        else visitedCaves + nextCave
                    queue.add(Path(nextCave, nextVisited))
                }
            }
    }

    return count
}

fun main() = timed {
    val map = (DATAPATH / "2021/day12.txt").useLines {
        val ret = mutableMapOf<String, MutableSet<String>>()
        it.forEach { line ->
            val (a, b) = line.split("-", limit = 2)
            if (a !in ret.keys) {
                ret[a] = mutableSetOf()
            }
            ret[a]!!.add(b)
            if (b !in ret.keys) {
                ret[b] = mutableSetOf()
            }
            ret[b]!!.add(a)
        }
        ret
    }

    println("Part one: ${findPaths(map)}")
}
