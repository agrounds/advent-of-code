package com.groundsfam.advent.y2021.d12

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


private const val START = "start"
private const val END = "end"

private data class Path(val currCave: String, val visitedCaves: Set<String>, val repeatedSmallCave: Boolean)

private fun findPaths(map: Map<String, Set<String>>, partTwo: Boolean): Int {
    val queue = ArrayDeque<Path>()
    // if partTwo = false, pretend we've already visited
    // a small cave twice
    queue.add(Path(START, setOf(START), !partTwo))
    var count = 0

    while (queue.isNotEmpty()) {
        val (currCave, visitedCaves, repeatedSmallCave) = queue.removeFirst()
        // all adjacent caves
        map[currCave]!!
            // If repeatedSmallCave = false, we do not need to filter out
            // previously visited small caves.
            // We'll handle setting this to true for repeated small caves below.
            .filter { !repeatedSmallCave || it !in visitedCaves }
            .forEach { nextCave ->
                if (nextCave == END) {
                    // end of the line -- don't add any new paths to the queue
                    // and increment the path count
                    count++
                } else {
                    // only add this cave to set of visited caves if it is
                    // a small cave
                    val nextVisited =
                        if (nextCave[0].isUpperCase()) visitedCaves
                        else visitedCaves + nextCave
                    // if this cave was previously visited, then we've used up
                    // our one allowed small cave revisit
                    val nextRepeatSmall = repeatedSmallCave || nextCave in visitedCaves
                    queue.add(Path(nextCave, nextVisited, nextRepeatSmall))
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
            // do not add any back connections to START
            // we will never return there
            if (b != START) {
                ret[a]!!.add(b)
            }
            if (b !in ret.keys) {
                ret[b] = mutableSetOf()
            }
            // do not add any back connections to START
            // we will never return there
            if (a != START) {
                ret[b]!!.add(a)
            }
        }
        ret
    }

    println("Part one: ${findPaths(map, partTwo = false)}")
    println("Part two: ${findPaths(map, partTwo = true)}")
}
