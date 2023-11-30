package com.groundsfam.advent.y2020.d17

import com.groundsfam.advent.DATAPATH
import java.util.ArrayDeque
import java.util.Queue
import kotlin.io.path.div
import kotlin.io.path.useLines

private typealias Point = Triple<Int, Int, Int>
private typealias State = Set<Point>

private fun State.next(): State {
    val thisState = this
    val nextState = mutableSetOf<Point>()
    val queue: Queue<Point> = ArrayDeque<Point>().apply {
        addAll(thisState)
    }
    val prevQueued = mutableSetOf<Point>().apply {
        addAll(thisState)
    }

    while (queue.isNotEmpty()) {
        queue.poll().let { point ->
            val (x, y, z) = point
            val activeNeighbors = (-1..1).sumOf { dx ->
                (-1..1).sumOf { dy ->
                    (-1..1).count { dz ->
                        val neighbor = Point(x + dx, y + dy, z + dz)
                        if (point in thisState && neighbor !in prevQueued) {
                            queue.add(neighbor)
                            prevQueued.add(neighbor)
                        }
                        neighbor != point && neighbor in thisState
                    }
                }
            }

            when {
                point in thisState && (activeNeighbors in setOf(2, 3)) ->
                    nextState.add(point)
                point !in thisState && activeNeighbors == 3 ->
                    nextState.add(point)
                else -> {} // don't add to next state
            }
        }
    }

    return nextState
}

private data class Point4(val x: Int, val y: Int, val z: Int, val w: Int)
private typealias State4 = Set<Point4>

private fun State4.next4(): State4 {
    val thisState = this
    val nextState = mutableSetOf<Point4>()
    val queue: Queue<Point4> = ArrayDeque<Point4>().apply {
        addAll(thisState)
    }
    val prevQueued = mutableSetOf<Point4>().apply {
        addAll(thisState)
    }

    while (queue.isNotEmpty()) {
        queue.poll().let { point ->
            val (x, y, z, w) = point
            val activeNeighbors = (-1..1).sumOf { dx ->
                (-1..1).sumOf { dy ->
                    (-1..1).sumOf { dz ->
                        (-1..1).count { dw ->
                            val neighbor = Point4(x + dx, y + dy, z + dz, w + dw)
                            if (point in thisState && neighbor !in prevQueued) {
                                queue.add(neighbor)
                                prevQueued.add(neighbor)
                            }
                            neighbor != point && neighbor in thisState
                        }
                    }
                }
            }


            when {
                point in thisState && (activeNeighbors in setOf(2, 3)) ->
                    nextState.add(point)
                point !in thisState && activeNeighbors == 3 ->
                    nextState.add(point)
                else -> {} // don't add to next state
            }
        }
    }

    return nextState
}

fun main() {
    val initialState = mutableSetOf<Point>()
    val initialState4 = mutableSetOf<Point4>()
    (DATAPATH / "2020/day17.txt").useLines { lines ->
        lines.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                if (c == '#') {
                    initialState.add(Point(x, y, 0))
                    initialState4.add(Point4(x, y, 0, 0))
                }
            }
        }
    }

    var state: State = initialState
    repeat(6) {
        state = state.next()
    }
    println("Part one: ${state.size}")

    var state4: State4 = initialState4
    repeat(6) {
        state4 = state4.next4()
    }
    println("Part two: ${state4.size}")
}