package com.groundsfam.advent

import com.groundsfam.advent.points.Point
import java.nio.file.Path
import kotlin.io.path.useLines

class Grid<T>(private val grid: MutableList<MutableList<T>> = mutableListOf()) : MutableList<MutableList<T>> by grid {
    operator fun get(point: Point): T = grid[point.y][point.x]
    operator fun set(p: Point, v: T) {
        grid[p.y][p.x] = v
    }

    inline fun <R> map(transform: (T) -> R): Grid<R> = Grid(
        this.mapTo(mutableListOf()) { row ->
            row.mapTo(mutableListOf(), transform)
        }
    )

    fun getRow(i: Int): MutableList<T> = grid[i]
    fun getCol(i: Int): MutableList<T> = grid.mapTo(mutableListOf()) { row ->
        row[i]
    }

    val numRows: Int = size
    val numCols: Int = if (isEmpty()) 0 else first().size
    val gridSize: Int = numRows * numCols

    // goes in order, rows then columns, like so:
    // (0, 0), (0, 1), (0, 2), ..., (1, 0), (1, 1), (1, 2), ...
    val pointIndices: List<Point> by lazy {
        this.flatMapIndexedTo(mutableListOf()) { y, row ->
            row.indices.map { x -> Point(x, y) }
        }
    }

    fun containsPoint(p: Point): Boolean =
        (p.x in 0 until numCols) && (p.y in 0 until numRows)

    @Suppress("unused")
    fun gridString(): String =
        grid.joinToString("\n") { it.joinToString("") }
}

/**
 * Creates a new copy of this [Grid] so you can mutate it
 * without affecting the original one.
 */
fun <T> Grid<T>.copy() = this.toGrid()

fun <T> List<List<T>>.toGrid() = Grid(this.mapTo(mutableListOf()) { it.toMutableList() })
fun Path.readGrid(): Grid<Char> =
    this.useLines { lines ->
        lines
            .mapTo(mutableListOf()) {
                it.toMutableList()
            }
            .let(::Grid)
    }

fun <T> Path.readGrid(transform: (Char) -> T): Grid<T> =
    this.useLines { lines ->
        lines
            .mapTo(mutableListOf()) {
                it.mapTo(mutableListOf(), transform)
            }
            .let(::Grid)
    }
