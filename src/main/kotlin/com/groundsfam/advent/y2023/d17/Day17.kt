package com.groundsfam.advent.y2023.d17

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.Direction
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.points.go
import com.groundsfam.advent.grids.contains
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.grids.readGrid
import com.groundsfam.advent.timed
import java.util.PriorityQueue
import kotlin.io.path.div


/**
 * [pos] - The current position of the crucible in the grid
 * [prevDir] - The direction we previously traveled to get here
 * [prevDirSteps] - The number of steps in [prevDir] direction we've walked so far. Must stay below 4
 * per puzzle description.
 */
private data class State(val pos: Point, val prevDir: Direction?, val prevDirSteps: Int?)
private data class StateWithCost(val pos: Point, val prevDir: Direction?, val prevDirSteps: Int?, val estimatedHeatLoss: Long) :
    Comparable<StateWithCost> {
    override fun compareTo(other: StateWithCost): Int = when {
        estimatedHeatLoss < other.estimatedHeatLoss -> -1
        estimatedHeatLoss > other.estimatedHeatLoss -> 1
        else -> 0
    }
}

private fun State.withCost(estimatedHeatLoss: Long) = StateWithCost(pos, prevDir, prevDirSteps, estimatedHeatLoss)
private fun StateWithCost.withoutCost() = State(pos, prevDir, prevDirSteps)

private fun minHeatLossPath(grid: Grid<Int>, minStraightLine: Int, maxStraightLine: Int): Long {
    val goal = Point(grid.numCols - 1, grid.numRows - 1)

    val queue = PriorityQueue<StateWithCost>()
    val gScores: MutableMap<State, Long> = mutableMapOf()
    val fScores: MutableMap<State, Long> = mutableMapOf()
    val cameFrom: MutableMap<State, State> = mutableMapOf()

    // manhattan distance from p to the bottom right corner
    fun hScore(p: Point): Long =
        (goal.x - p.x + goal.y - p.y)
            .toLong()

    val start = State(Point(0, 0), null, null)
    queue.add(start.withCost(0))
    gScores[start] = 0
    fScores[start] = hScore(start.pos)

    while (queue.isNotEmpty()) {
        val (pos, prevDir, prevDirSteps, estimatedHeatLoss) = queue.poll()
        val state = State(pos, prevDir, prevDirSteps)
        if (pos == goal) {
            return estimatedHeatLoss
        }

        val allowedDirections = when {
            // prevDir and prevDirSteps will be null or non-null together
            prevDirSteps == null || prevDir == null -> Direction.entries
            prevDirSteps < minStraightLine -> listOf(prevDir)
            prevDirSteps < maxStraightLine -> listOf(prevDir, prevDir.cw, prevDir.ccw)
            else -> listOf(prevDir.cw, prevDir.ccw)
        }
        allowedDirections
            .filter { pos.go(it) in grid }
            .forEach { dir ->
                val nextPos = pos.go(dir)
                val nextState = State(nextPos, dir,
                    if (dir == prevDir) prevDirSteps!! + 1 else 1
                )
                val tentativeG = gScores[state]!! + grid[nextPos]
                val oldG = gScores[nextState]
                if (oldG == null || tentativeG < oldG) {
                    cameFrom[nextState] = state
                    val nextEstimatedHeatLoss = tentativeG + hScore(nextPos)
                    gScores[nextState] = tentativeG
                    fScores[nextState] = nextEstimatedHeatLoss
                    queue.add(nextState.withCost(nextEstimatedHeatLoss))
                }
            }
    }

    throw RuntimeException("No path found")
}

fun main() = timed {
    val grid = (DATAPATH / "2023/day17.txt")
        .readGrid(Char::digitToInt)

    println("Part one: ${minHeatLossPath(grid, 0, 3)}")
    println("Part two: ${minHeatLossPath(grid, 4, 10)}")
}
