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
data class Edge(val horizontal: Boolean, val d: Int, val range: IntRange)

// return point closest to (0, 0), if one exists, in the intersection of these edges
// exclude the point (0, 0) itself
fun Edge.intersect(that: Edge): Point? {
    if (this.horizontal == that.horizontal) {
        if (this.d != that.d) return null
        val a = this.d
        return this.range.rangeIntersect(that.range)?.let { intRange ->
            val b = when {
                intRange.first > 0 -> intRange.first
                intRange.last < 0 -> intRange.last
                a == 0 -> when {
                    1 in intRange -> 1
                    -1 in intRange -> -1
                    else -> null
                }
                else -> 0
            }
            b?.let {
                if (horizontal) Point(b, a) else Point(a, b)
            }
        }
    }

    if (this.d in that.range && that.d in this.range) {
        val intP =
            if (this.horizontal) Point(that.d, this.d)
            else Point(this.d, that.d)
        return intP.takeIf { it != Point(0, 0) }
    }

    return null
}

fun findIntersection(wire1: List<PathPart>, wire2: List<PathPart>): Int {
    val edges = listOf(wire1, wire2).map { wire ->
        var p = Point(0, 0)
        wire.map { (dir, dist) ->
            val nextP = p + dist * dir.asPoint()

            val edge = if (dir == Direction.UP || dir == Direction.DOWN) {
                val (from, to) = listOf(p.y, nextP.y).sorted()
                Edge(false, p.x, from..to)
            } else {
                val (from, to) = listOf(p.x, nextP.x).sorted()
                Edge(true, p.y, from..to)
            }
            p = nextP
            edge
        }
    }

    return edges[0].minOf { edge1 ->
        edges[1].minOf { edge2 ->
            edge1.intersect(edge2)
                ?.let { (x, y) -> abs(x) + abs(y) }
                ?: Int.MAX_VALUE
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
    println("Part one: ${findIntersection(wire1, wire2)}")
}
