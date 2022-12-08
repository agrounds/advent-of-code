package advent.y2022.d08

import advent.DATAPATH
import kotlin.io.path.div
import kotlin.io.path.useLines

fun countVisibleTrees(grid: List<List<Int>>): Int {
    val size = grid.size  // grid is a square, so rows and columns have same size
    val visible = Array(size) { BooleanArray(size) }

    (0 until size).forEach { i ->
        var maxTree = -1
        // rows left-to-right
        (0 until size).forEach { j ->
            val tree = grid[i][j]
            if (tree > maxTree) {
                visible[i][j] = true
                maxTree = tree
            }
        }
        maxTree = -1
        // rows right-to-left
        (size-1 downTo 0).forEach { j ->
            val tree = grid[i][j]
            if (tree > maxTree) {
                visible[i][j] = true
                maxTree = tree
            }
        }
    }

    (0 until size).forEach { j ->
        var maxTree = -1
        // columns top-to-bottom
        (0 until size).forEach { i ->
            val tree = grid[i][j]
            if (tree > maxTree) {
                visible[i][j] = true
                maxTree = tree
            }
        }
        maxTree = -1
        // columns bottom-to-top
        (size-1 downTo 0).forEach { i ->
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
