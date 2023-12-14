@file:Suppress("MemberVisibilityCanBePrivate")

package com.groundsfam.advent.y2023.d14

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.Direction.DOWN
import com.groundsfam.advent.Direction.LEFT
import com.groundsfam.advent.Direction.RIGHT
import com.groundsfam.advent.Direction.UP
import com.groundsfam.advent.Grid
import com.groundsfam.advent.copy
import com.groundsfam.advent.go
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.readGrid
import com.groundsfam.advent.timed
import kotlin.io.path.div


private fun Grid<*>.directedIndices(direction: Direction): List<Point> = when (direction) {
    UP -> (0 until numRows).flatMap { y ->
        (0 until numCols).map { x ->
            Point(x, y)
        }
    }
    DOWN -> (0 until numRows).reversed().flatMap { y ->
        (0 until numCols).map { x ->
            Point(x, y)
        }
    }
    LEFT -> (0 until numCols).flatMap { x ->
        (0 until numRows).map { y ->
            Point(x, y)
        }
    }
    RIGHT -> (0 until numCols).reversed().flatMap { x ->
        (0 until numRows).map { y ->
            Point(x, y)
        }
    }
}

private fun Grid<Char>.toKey() = joinToString("") { it.joinToString("") }

private class Solution(private val originalPlatform: Grid<Char>) {
    val platform = originalPlatform.copy()
    var cycleCount = 0
        private set
    private var tiltDir: Direction = UP
    private var repetitionPeriod: Int? = null

    fun tilt() {
        platform.directedIndices(tiltDir).forEach { p ->
            if (platform[p] == 'O') {
                // to will be the point that p slides north to
                var to = p
                var toNext = to.go(tiltDir)
                while (platform.containsPoint(toNext) && platform[toNext] == '.') {
                    to = toNext
                    toNext = to.go(tiltDir)
                }
                platform[to] = 'O'
                if (p != to) {
                    platform[p] = '.'
                }
            }
        }
        tiltDir = tiltDir.ccw
    }

    fun cycle() {
        do {
            tilt()
        } while (tiltDir != UP)
        cycleCount++
    }

    // returns the period of repetition
    fun findRepetition(): Int {
        repetitionPeriod?.also {
            return it
        }

        val cycles = mutableMapOf(
            originalPlatform.toKey() to 0,
        )
        // handle rest of first cycle
        cycle()
        assert(cycleCount == 1)

        // continue cycling until a repeated configuration is found
        var key = platform.toKey()
        while (key !in cycles.keys) {
            cycles[key] = cycleCount
            cycle()
            key = platform.toKey()
        }

        return (cycleCount - cycles[key]!!)
            .also { repetitionPeriod = it }
    }

    fun load(): Int =
        platform.pointIndices.sumOf { p ->
            if (platform[p] == 'O') {
                platform.numRows - p.y
            } else {
                0
            }
        }
}

fun main() = timed {
    val solution = (DATAPATH / "2023/day14.txt")
        .readGrid()
        .let(::Solution)

    solution.tilt()
    println("Part one: ${solution.load()}")

    val period = solution.findRepetition()
    repeat((1_000_000_000 - solution.cycleCount) % period) {
        solution.cycle()
    }
    println("Part two: ${solution.load()}")
}
