package com.groundsfam.advent.y2016.d08

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.grids.Grid
import com.groundsfam.advent.grids.count
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

sealed class Operation
data class Rect(val width: Int, val height: Int) : Operation()
data class RotateRow(val rowNum: Int, val amount: Int) : Operation()
data class RotateCol(val colNum: Int, val amount: Int) : Operation()

fun parse(line: String): Operation {
    val parts = line.split(" ")
    if (parts[0] == "rect") {
        val (w, h) = parts[1].split("x").map(String::toInt)
        return Rect(w, h)
    }
    val num = parts[2].substring(2).toInt()
    val amount = parts[4].toInt()
    if (parts[1] == "row") {
        return RotateRow(num, amount)
    }
    return RotateCol(num, amount)
}

fun runOperations(operations: List<Operation>): Grid<Char> {
    val grid = Grid(6, 50) { '.' }

    operations.forEach { op ->
        when (op) {
            is Rect -> {
                (0 until op.width).forEach { x ->
                    (0 until op.height).forEach { y ->
                        grid[Point(x, y)] = '#'
                    }
                }
            }
            is RotateRow -> {
                val y = op.rowNum
                val oldRow = grid.getRow(y).toList()
                (0 until grid.numCols).forEach { x ->
                    grid[Point(x, y)] = oldRow[(x + grid.numCols - op.amount) % grid.numCols]
                }
            }
            is RotateCol -> {
                val x = op.colNum
                val oldCol = grid.getCol(x)
                (0 until grid.numRows).forEach { y ->
                    grid[Point(x, y)] = oldCol[(y + grid.numRows - op.amount) % grid.numRows]
                }
            }
        }
    }

    return grid
}

fun main() = timed {
    val operations = (DATAPATH / "2016/day08.txt").useLines { lines ->
        lines.mapTo(mutableListOf(), ::parse)
    }
    val grid = runOperations(operations)
    println("Part one: ${grid.count { it == '#' }}")
    println("Part two:")
    println(grid.gridString())
}
