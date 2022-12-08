package advent.y2022.d08

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun countVisibleTrees(grid: List<List<Int>>): Int {
    val size = grid.size  // grid is a square, so rows and columns have same size
    val visible = Array(size) { BooleanArray(size) }

    /*
     * Collection of walks to take across the grid of trees. A walk is a list of
     * coordinate pairs (i, j) of trees to consider, in a specified order.
     */
    (
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
    }.forEach { walk ->
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


fun main() {
    val grid = (DATAPATH / "2022/day08.txt").useLines { lines ->
        lines.toList().map { line ->
            line.map { it - '0' }
        }
    }
    countVisibleTrees(grid).also { println("Part one: $it") }
}
