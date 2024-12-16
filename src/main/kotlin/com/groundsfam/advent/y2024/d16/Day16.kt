package com.groundsfam.advent.y2024.d16

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.grids.*
import com.groundsfam.advent.points.*
import com.groundsfam.advent.timed
import java.util.PriorityQueue
import kotlin.io.path.div
import kotlin.math.min

data class State(val p: Point, val dir: Direction)
data class Path(val state: State, val pathLen: Long)

fun bestPath(grid: Grid<Char>): Pair<Long, Int> {
    val start = grid.pointOfFirst { it == 'S' }
    val end = grid.pointOfFirst { it == 'E' }

    val queue = PriorityQueue<Path>(compareBy { it.pathLen })
    // states get added to visited the first time
    // a path with that state gets polled from the queue
    // ensuring we only consider neighbors the first time we see this state
    // this is guaranteed to be the best path because we're using
    // a priority queue
    val visited = mutableSetOf<State>()
    // map from a state to the length of the best path from the
    // start state to that state, out of all such paths we know of
    // so far
    val bestPath = mutableMapOf<State, Long>()
    // set of points that are in at least one of the best-known-so-far
    // paths from the start state to the given state
    val bestSeats = mutableMapOf<State, Set<Point>>()
    // signal that helps us end the algorithm
    // any path with length longer than the best path to end
    // can be discarded
    var endPathLen: Long? = null

    val startState = State(start, Direction.RIGHT)
    bestPath[startState] = 0
    bestSeats[startState] = setOf(start)
    queue.add(Path(startState, 0))

    while (queue.isNotEmpty()) {
        val (state, pathLen) = queue.poll()
        val (p, dir) = state
        if (!visited.add(state)) continue
        if (pathLen > (endPathLen ?: Long.MAX_VALUE)) continue

        // (state, pathLen) pairs
        val nextStates =
            if (p == end) emptyList()
            else listOf(
                Pair(State(p, dir.cw), pathLen + 1000),
                Pair(State(p, dir.ccw), pathLen + 1000),
            ) +
                if (grid[p.go(dir)] == '#') emptyList()
                else listOf(Pair(State(p.go(dir), dir), pathLen + 1))

        nextStates.forEach { (nextState, nextPathLen) ->
            if (nextState.p == end) {
                endPathLen = min(nextPathLen, endPathLen ?: Long.MAX_VALUE)
            }
            if (nextState !in bestPath || bestPath[nextState]!! > nextPathLen) {
                bestPath[nextState] = nextPathLen
                bestSeats[nextState] = bestSeats[state]!! + setOf(nextState.p)
                queue.add(Path(nextState, nextPathLen))
            }
            if (bestPath[nextState] == nextPathLen) {
                bestSeats[nextState] = bestSeats[nextState]!! + bestSeats[state]!!
                queue.add(Path(nextState, nextPathLen))
            }
        }
    }

    val endState = Direction.entries
        .minBy { bestPath[State(end, it)] ?: Long.MAX_VALUE }
        .let { State(end, it) }

    return Pair(bestPath[endState]!!, bestSeats[endState]!!.size)
}

fun main() = timed {
    val grid = (DATAPATH / "2024/day16.txt").readGrid()
    val (len, numSeats) = bestPath(grid)
    println("Part one: $len")
    println("Part two: $numSeats")
}
