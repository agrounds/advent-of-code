package com.groundsfam.advent

import com.groundsfam.advent.points.Point

@JvmInline
value class Grid<T>(private val grid: MutableList<MutableList<T>>) : MutableList<MutableList<T>> by grid {
    operator fun get(point: Point): T = grid[point.y][point.x]
    operator fun set(p: Point, v: T) {
        grid[p.y][p.x] = v
    }

    inline fun <R> map(transform: (T) -> R): Grid<R> = Grid(
        this.mapTo(mutableListOf()) { row ->
            row.mapTo(mutableListOf(), transform)
        }
    )

    val pointIndices: Set<Point>
        get() = this.flatMapIndexedTo(mutableSetOf()) { y, row ->
            row.indices.map { x -> Point(x, y) }
        }
}

fun <T> List<List<T>>.toGrid() = Grid(this.mapTo(mutableListOf()) { it.toMutableList() })
