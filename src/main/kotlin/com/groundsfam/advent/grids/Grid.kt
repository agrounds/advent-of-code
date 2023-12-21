package com.groundsfam.advent.grids

import com.groundsfam.advent.points.Point

class Grid<T>(private val grid: MutableList<MutableList<T>> = mutableListOf()) : MutableList<MutableList<T>> by grid {
    constructor(numRows: Int, numCols: Int, init: (Point) -> T) : this(
        (0 until numCols).mapTo(mutableListOf()) { y ->
            (0 until numRows).mapTo(mutableListOf()) { x ->
                init(Point(x, y))
            }
        }
    )

    operator fun get(point: Point): T = grid[point.y][point.x]
    operator fun set(p: Point, v: T) {
        grid[p.y][p.x] = v
    }


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

    @Suppress("unused")
    fun gridString(): String =
        grid.joinToString("\n") { it.joinToString("") }
}
