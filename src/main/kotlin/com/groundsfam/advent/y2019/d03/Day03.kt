package com.groundsfam.advent.y2019.d03

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.asPoint
import com.groundsfam.advent.points.*
import com.groundsfam.advent.rangeIntersect
import com.groundsfam.advent.timed
import com.groundsfam.advent.toDirection
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs

data class PathPart(val direction: Direction, val distance: Int)
data class Edge(val direction: Direction, val d: Int, val range: IntRange, val prevSteps: Int) {
    val horizontal = direction == Direction.LEFT || direction == Direction.RIGHT
}

// return set of points of intersection of the edges
fun Edge.intersect(that: Edge): Set<Point> {
    if (this.horizontal == that.horizontal) {
        if (this.d != that.d) return emptySet()

        val a = this.d
        return this.range.rangeIntersect(that.range)?.let { intRange ->
            intRange.mapTo(mutableSetOf()) { b ->
                if (horizontal) Point(b, a) else Point(a, b)
            }
        } ?: emptySet()
    }

    if (this.d in that.range && that.d in this.range) {
        val intP =
            if (this.horizontal) Point(that.d, this.d)
            else Point(this.d, that.d)
        return setOf(intP)
    }

    return emptySet()
}

fun findIntersection(wire1: List<PathPart>, wire2: List<PathPart>, partTwo: Boolean): Int {
    val edges = listOf(wire1, wire2).map { wire ->
        var p = Point(0, 0)
        var steps = 0
        wire.map { (dir, dist) ->
            val nextP = p + dist * dir.asPoint()

            val edge = if (dir == Direction.UP || dir == Direction.DOWN) {
                val (from, to) = listOf(p.y, nextP.y).sorted()
                Edge(dir, p.x, from..to, steps)
            } else {
                val (from, to) = listOf(p.x, nextP.x).sorted()
                Edge(dir, p.y, from..to, steps)
            }
            p = nextP
            steps += dist
            edge
        }
    }

    return edges[0].minOf { edge1 ->
        edges[1].minOf { edge2 ->
            edge1.intersect(edge2)
                .filterNot { it == Point(0, 0) }
                .minOfOrNull { (x, y) ->
                    if (partTwo) {
                        listOf(edge1, edge2).sumOf {
                            val stepsToPoint = when (it.direction) {
                                Direction.RIGHT -> x - it.range.first
                                Direction.LEFT -> it.range.last - x
                                Direction.DOWN -> y - it.range.first
                                Direction.UP -> it.range.last - y
                            }
                            it.prevSteps + stepsToPoint
                        }
                    } else {
                        abs(x) + abs(y)
                    }
                } ?: Int.MAX_VALUE
        }
    }
}

fun main() = timed {
    val (wire1, wire2) = (DATAPATH / "2019/day03.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            line.split(",").map { part ->
                PathPart(part[0].toDirection()!!, part.substring(1).toInt())
            }
        }
    }
    println("Part one: ${findIntersection(wire1, wire2, false)}")
    println("Part two: ${findIntersection(wire1, wire2, true)}")
}
