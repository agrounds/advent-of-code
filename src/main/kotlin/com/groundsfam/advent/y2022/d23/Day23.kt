package com.groundsfam.advent.y2022.d23

import com.groundsfam.advent.*
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.e
import com.groundsfam.advent.points.n
import com.groundsfam.advent.points.ne
import com.groundsfam.advent.points.nw
import com.groundsfam.advent.points.s
import com.groundsfam.advent.points.se
import com.groundsfam.advent.points.sw
import com.groundsfam.advent.points.w
import kotlin.io.path.div
import kotlin.io.path.useLines

fun moveElves(startingPositions: Set<Point>, partOne: Boolean): Int {
    var elves = startingPositions.toList()
    var elvesSet = startingPositions.toSet()

    fun clearAround(position: Point) =
        (setOf(position.n, position.ne, position.e, position.se,
            position.s, position.sw, position.w, position.nw) intersect elvesSet).isEmpty()

    val directions = mutableListOf('N', 'S', 'W', 'E')
    fun clearInDirection(position: Point, direction: Char) =  when (direction) {
        'N' -> (setOf(position.n, position.ne, position.nw) intersect elvesSet).isEmpty()
        'S' -> (setOf(position.s, position.se, position.sw) intersect elvesSet).isEmpty()
        'W' -> (setOf(position.w, position.nw, position.sw) intersect elvesSet).isEmpty()
        'E' -> (setOf(position.e, position.ne, position.se) intersect elvesSet).isEmpty()
        else -> false
    }
    fun next(position: Point, direction: Char) = when (direction) {
        'N' -> position.n
        'S' -> position.s
        'W' -> position.w
        'E' -> position.e
        else -> position
    }

    var roundNum = 1
    while (!partOne || roundNum <= 10) {
        val proposedPositions = mutableListOf<Point?>()
        val proposedCount = mutableMapOf<Point, Int>()
        elves.forEach { position ->
            val next = directions.firstOrNull { clearInDirection(position, it) }
                ?.let { next(position, it) }
                ?.takeUnless { clearAround(position) }  // do not move if no one is around
            proposedPositions.add(next)
            if (next != null) {
                proposedCount[next] = (proposedCount[next] ?: 0) + 1
            }
        }

        directions.add(directions.removeFirst())

        if (!partOne && proposedPositions.all { it == null }) {
            return roundNum
        }

        elves = List(elves.size) { i ->
            val next = proposedPositions[i]
            if (next != null && proposedCount[next] == 1) {
                next
            } else {
                elves[i]
            }
        }
        elvesSet = elves.toSet()
        roundNum++
    }

    val rectangle = (elves.maxOf { it.x } - elves.minOf { it.x } + 1) *
        (elves.maxOf { it.y } - elves.minOf { it.y } + 1)
    return rectangle - elves.size
}

// for debug
fun drawElves(elves: Set<Point>) =
    (0..elves.maxOf { it.y }).joinToString("\n") { y ->
        (0..elves.maxOf { it.x }).joinToString("") { x ->
            if (Point(x, y) in elves) "#"
            else "."
        }
    }


fun main() = timed {
    // set of points the elves are standing at
    val elves = (DATAPATH / "2022/day23.txt").useLines { lines ->
        lines.toList().flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c -> if (c == '#') Point(x, y) else null }
        }.toSet()
    }
    println("Part one: ${moveElves(elves, true)}")
    println("Part two: ${moveElves(elves, false)}")
}
