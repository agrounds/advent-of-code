package com.groundsfam.advent.grids

import com.groundsfam.advent.points.Point
import java.nio.file.Path
import kotlin.io.path.useLines

/**
 * Creates a new copy of this [Grid] so you can mutate it
 * without affecting the original one.
 */
fun <T> Grid<T>.copy() = this.toGrid()
fun <T> List<List<T>>.toGrid() = Grid(this.mapTo(mutableListOf()) { it.toMutableList() })

fun <T> Grid<T>.containsPoint(p: Point): Boolean =
    (p.x in 0 until numCols) && (p.y in 0 until numRows)

fun <T> Grid<T>.maybeGet(p: Point): T? =
    if (containsPoint(p)) this[p]
    else null

inline fun <T, R> Grid<T>.map(transform: (T) -> R): Grid<R> = Grid(
    this.mapTo(mutableListOf()) { row ->
        row.mapTo(mutableListOf(), transform)
    }
)

inline fun <T, R> Grid<T>.mapIndexed(transform: (Point, T) -> R): Grid<R> = Grid(
    this.mapIndexedTo(mutableListOf()) { y, row ->
        row.mapIndexedTo(mutableListOf()) { x, v ->
            transform(Point(x, y), v)
        }
    }
)

fun <T> Grid<T>.count(predicate: (T) -> Boolean): Int =
    this.sumOf { row ->
        row.count(predicate)
    }

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

fun <T> Grid<T>.pointOfFirst(predicate: (T) -> Boolean): Point =
    pointIndices.first { p ->
        predicate(this[p])
    }

fun <T> Grid<T>.pointOfLast(predicate: (T) -> Boolean): Point =
    pointIndices.last { p ->
        predicate(this[p])
    }
