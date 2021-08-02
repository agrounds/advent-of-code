package advent.y2020.d17

import advent.y2020.DATAPATH
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

fun main() {
    val initialState: State = (DATAPATH / "day17.txt").useLines { lines ->
        lines.flatMapIndexed { y, line ->
            line.mapIndexedNotNull { x, c ->
                when (c) {
                    '#' -> Triple(x, y, 0)
                    else -> null
                }
            }
        }.toSet()
    }

    var state = initialState
    repeat(6) {
        state = state.next()
    }
    println("Part one: ${state.size}")
}