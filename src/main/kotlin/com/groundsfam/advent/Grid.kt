package com.groundsfam.advent

import com.groundsfam.advent.points.Point

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

    val pointIndices: Set<Point> by lazy {
        this.flatMapIndexedTo(mutableSetOf()) { y, row ->
            row.indices.map { x -> Point(x, y) }
        }
    }

    fun containsPoint(p: Point): Boolean =
        (p.x in 0 until numCols) && (p.y in 0 until numRows)
}

fun <T> List<List<T>>.toGrid() = Grid(this.mapTo(mutableListOf()) { it.toMutableList() })
