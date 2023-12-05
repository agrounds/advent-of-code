package com.groundsfam.advent.y2022.d08

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import java.util.ArrayDeque
import java.util.Deque
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs

/**
 * Collection of walks to take across the grid of trees. A walk is a list of
 * coordinate pairs (i, j) of trees to consider, in a specified order.
 */
fun getWalks(size: Int): List<List<Pair<Int, Int>>> = (
    // left to right
    (0 until size).map { i ->
        (0 until size).map { j -> i to j }
    } +
        // right to left
        (0 until size).map { i ->
            (size - 1 downTo 0).map { j -> i to j }
        }
    ).let { walks ->
        // top to bottom and bottom to top
        walks + walks.map { walk ->
            walk.map { (i, j) -> j to i }
        }
    }

fun countVisibleTrees(grid: List<List<Int>>): Int {
    val size = grid.size  // grid is a square, so rows and columns have same size
    val visible = Array(size) { BooleanArray(size) }

    /*
     * Collection of walks to take across the grid of trees. A walk is a list of
     * coordinate pairs (i, j) of trees to consider, in a specified order.
     */
    getWalks(size).forEach { walk ->
        var maxTree = -1
        walk.forEach { (i, j) ->
            val tree = grid[i][j]
            if (tree > maxTree) {
                visible[i][j] = true
                maxTree = tree
            }
        }
    }

    return visible.sumOf { row -> row.count { it } }
}

fun maxScenicScore(grid: List<List<Int>>): Int {
    val size = grid.size
    val scores = Array(size) { IntArray(size) { 1 } }

    infix fun Pair<Int, Int>.compareHeight(other: Pair<Int, Int>): Int {
        val (thisX, thisY) = this
        val (otherX, otherY) = other
        return grid[thisX][thisY] - grid[otherX][otherY]
    }

    getWalks(size).forEach { walk ->
        val prevMaxes: Deque<Pair<Int, Int>> = ArrayDeque()
        // determine direction of this walk: xDirection or yDirection, and increasing or decreasing
        val xDirection = walk[0].first != walk[1].first
        val increasing =
            if (xDirection) walk[0].first < walk[1].first
            else walk[0].second < walk[1].second

        // find the (positive) distance between the two points on the current walk, or the distance
        // from the point to the beginning of the walk in the case that other is null
        infix fun Pair<Int, Int>.diff(other: Pair<Int, Int>?): Int {
            val (thisX, thisY) = this
            if (other == null) {
                return when {
                    xDirection && increasing -> thisX
                    xDirection && !increasing -> size - thisX - 1
                    !xDirection && increasing -> thisY
                    else -> size - thisY - 1
                }
            }
            val (otherX, otherY) = other

            return if (xDirection) abs(thisX - otherX)
            else abs(thisY - otherY)
        }

        walk.forEach { point ->
            val (i, j) = point
            var prevMax: Pair<Int, Int>? = null
            while (prevMaxes.isNotEmpty() && point compareHeight prevMaxes.peek() >= 0) {
                prevMax = prevMaxes.pop()
            }
            when {
                // we encountered another tree of same height as this one, so
                // the score is the distance from this tree to the previous one of same height
                prevMax != null && point compareHeight prevMax == 0 -> {
                    scores[i][j] *= point diff prevMax
                }
                // either we're the tallest tree so far, or prevMax is a tree shorter than this
                // one. the score is the distance from this tree to the previous _strictly_ taller
                // tree (which is prevMaxes.peek()), or this is the tallest tree so far and we should
                // diff null. point diff peek() handles both cases.
                else -> {
                    scores[i][j] *= point diff prevMaxes.peek()
                }
            }
            prevMaxes.push(point)
        }
    }

    return scores.maxOf { it.maxOrNull()!! }
}


fun main() = timed {
    val grid = (DATAPATH / "2022/day08.txt").useLines { lines ->
        lines.toList().map { line ->
            line.map { it - '0' }
        }
    }
    countVisibleTrees(grid)
        .also { println("Part one: $it") }
    maxScenicScore(grid)
        .also { println("Part two: $it") }
}
