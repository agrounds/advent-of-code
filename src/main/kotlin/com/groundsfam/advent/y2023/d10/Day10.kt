package com.groundsfam.advent.y2023.d10

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.Direction.DOWN
import com.groundsfam.advent.Direction.LEFT
import com.groundsfam.advent.Direction.RIGHT
import com.groundsfam.advent.Direction.UP
import com.groundsfam.advent.go
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.points.get
import com.groundsfam.advent.points.pointIndices
import com.groundsfam.advent.points.set
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


// contains the two directions that this pipe connects
typealias Pipe = Set<Direction>
fun Pipe(vararg dirs: Direction): Pipe = setOf(*dirs)


fun Char.toPipe(): Pipe =
    when (this) {
        '|' -> Pipe(UP, DOWN)
        '-' -> Pipe(LEFT, RIGHT)
        'F' -> Pipe(RIGHT, DOWN)
        '7' -> Pipe(LEFT, DOWN)
        'J' -> Pipe(LEFT, UP)
        'L' -> Pipe(RIGHT, UP)
        else -> Pipe()
    }

class Solution(private val start: Point, private val pipes: List<List<Pipe>>) {
    // if loop[point] is not null, then point is in the main loop
    // and the direction is the where the loop is going to next
    // for corners, always choose the vertical direction, for purpose
    // of finding the interior points
    private val loop: List<List<Direction?>>
    private val loopLength: Int
    // the direction, either UP or DOWN, of the leftmost edge of the loop
    private val leftLoopDir: Direction

    init {
        val _loop: MutableList<MutableList<Direction?>> =
            pipes.mapTo(mutableListOf()) { row ->
                row.mapTo(mutableListOf()) { null }
            }
        var _loopLength = 0

        var prevDir: Direction = Direction.entries
            .filter { dir ->
                val adjacent = start.go(dir)
                adjacent in pipes.pointIndices && -dir in pipes[adjacent]
            }.let { dirs ->
                // choose vertical direction if possible
                dirs.firstOrNull { it.isVertical() } ?: dirs.first()
            }

        _loop[start] = prevDir
        var curr: Point = start.go(prevDir)
        _loopLength++
        while (curr != start) {
            val nextDir = pipes[curr].first { it != -prevDir }
            // choose vertical direction if possible
            _loop[curr] = if (prevDir.isVertical()) prevDir else nextDir
            prevDir = nextDir
            curr = curr.go(prevDir)
            _loopLength++
        }

        loop = _loop
        loopLength = _loopLength
        leftLoopDir = loop.firstNotNullOf { row ->
            row.firstOrNull { it != null }
        }
    }

    fun farthestLoopPoint(): Int = loopLength / 2

    fun loopInterior(): Int =
        pipes.indices.sumOf { y ->
            val row = pipes[y]
            var count = 0
            var prevVerticalDir: Direction? = null
            // TODO fix this with new vertical-oriented strategy
            row.forEachIndexed { x, c ->
                when (val d = loop[Point(x, y)]) {
                    null -> {
                        if (prevVerticalDir == leftLoopDir) {
                            count++
                        }
                    }
                    UP, DOWN -> {
                        prevVerticalDir = d
                    }
                    else -> {} // ignore
                }
            }
            count
        }
}


fun main() = timed {
    val solution = (DATAPATH / "2023/day10.txt").useLines { lines ->
        var start: Point? = null
        val pipes = lines
            .mapIndexed { y, line ->
                line.mapIndexed { x, c ->
                    if (c == 'S') {
                        start = Point(x, y)
                    }
                    c.toPipe()
                }
            }
            .toList()
        Solution(start!!, pipes)
    }
    println("Part one: ${solution.farthestLoopPoint()}")
    println("Part two: ${solution.loopInterior()}")
}
