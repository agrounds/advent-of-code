package com.groundsfam.advent.y2023.d18

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.Direction.DOWN
import com.groundsfam.advent.Direction.LEFT
import com.groundsfam.advent.Direction.RIGHT
import com.groundsfam.advent.Direction.UP
import com.groundsfam.advent.asPoint
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.max
import kotlin.math.min


private data class Trench(val direction: Direction, val length: Int, val color: String)
private fun Trench.toVector() = direction.asPoint() * length
private fun toEdge(point1: Point, point2: Point): IntRange =
    min(point1.x, point2.x)..max(point1.x, point2.x)

private class Solution(digPlan: List<Trench>) {
    /**
     * horizontal edges, specifically
     *
     *  `edges[y]` is equal to a sorted list of all horizontal edges of that `y` value
    */
    private val edges: Map<Int, List<IntRange>>
    init {
        val _edges = mutableMapOf<Int, MutableList<IntRange>>()
        digPlan.chunked(2).fold(Point(0, 0)) { edgeStart, trenches ->
            val (trench1, trench2) = trenches
            val edgeEnd = edgeStart + trench1.toVector()
            if (edgeStart.y !in _edges.keys) {
                _edges[edgeStart.y] = mutableListOf()
            }
            _edges[edgeStart.y]!!.add(toEdge(edgeStart, edgeEnd))

            edgeEnd + trench2.toVector()
        }
        // sort the edge lists
        _edges.mapValues { (_, edges) -> edges.sortBy { it.first } }
        edges = _edges
    }

    fun lagoonVolume(): Long {
        val levels = edges.keys.sorted()
        var further = edges[levels.first()]!!
        var volume: Long = 0

        (1 until levels.size).forEach { il ->
            val prevLevel = levels[il - 1]
            val currLevel = levels[il]
            val prevEdges = further
            val currEdges = edges[currLevel]!!

//            println("prevEdges = $prevEdges, currEdges = $currEdges")
            volume += further.sumOf { it.last - it.first + 1 } * (currLevel - prevLevel)

            var ip = 0
            var ic = 0
            var wipEdge: IntRange? = null
            val oneRow = mutableListOf<IntRange>()  // I expect I'll be able to remove this and just add the lengths to the volume directly instead
            val nextFurther = mutableListOf<IntRange>()

            while (ip < prevEdges.size || ic < currEdges.size) {
                val prevEdge = prevEdges.getOrNull(ip)
                val currEdge = currEdges.getOrNull(ic)
                // pick which edge to use to extend/end wipEdge and advance its respective index
                val edge = when {
                    prevEdge == null -> {
                        ic++
                        currEdge!!
                    }
                    currEdge == null -> {
                        ip++
                        prevEdge
                    }
                    prevEdge.first <= currEdge.first -> {
                        ip++
                        prevEdge
                    }
                    else -> {
                        ic++
                        currEdge
                    }
                }
                val _wipEdge = wipEdge
                when {
                    _wipEdge == null -> {
                        wipEdge = edge
                    }
                    edge.first == _wipEdge.first -> when {
                        edge.last < _wipEdge.last -> {
                            oneRow.add(edge.first until edge.last)
                            wipEdge = edge.last.._wipEdge.last
                        }
                        edge.last == _wipEdge.last -> {
                            oneRow.add(edge)
                            wipEdge = null
                        }
                        else -> throw IllegalStateException("edge=$edge, wipEdge=$wipEdge")
                    }
                    edge.first in _wipEdge.first + 1 until _wipEdge.last -> {
                        nextFurther.add(_wipEdge.first..edge.first)
                        when {
                            edge.last < _wipEdge.last -> {
                                oneRow.add(edge.first + 1 until edge.last)
                                wipEdge = edge.last.._wipEdge.last
                            }
                            edge.last == _wipEdge.last -> {
                                oneRow.add(edge.first + 1..edge.last)
                                wipEdge = null
                            }
                            else -> throw IllegalStateException("edge=$edge, wipEdge=$wipEdge")
                        }
                    }
                    edge.first == _wipEdge.last -> {
                        wipEdge = _wipEdge.first..edge.last
                    }
                    edge.first > _wipEdge.last -> {
                        nextFurther.add(_wipEdge)
                        wipEdge = edge
                    }
                    else -> throw IllegalStateException("edge=$edge, wipEdge=$wipEdge")
                }
            }

            // if there's a wipEdge that never got completed above, add it too
            wipEdge?.let(nextFurther::add)

            volume += oneRow.sumOf { it.last - it.first + 1 }
            further = nextFurther
        }

        return volume
    }
}

fun main() = timed {
    val solution = (DATAPATH / "2023/day18.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            val (d, l, c) = line.split(" ")
            val dir = when (d) {
                "U" -> UP
                "D" -> DOWN
                "L" -> LEFT
                "R" -> RIGHT
                else -> throw RuntimeException("Parsing error: Invalid direction $d")
            }
            Trench(dir, l.toInt(), c.substring(2..7))
        }
    }
        .let(::Solution)
    println("Part one: ${solution.lagoonVolume()}")
}
