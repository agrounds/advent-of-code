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

operator fun <T> Grid<T>.contains(p: Point): Boolean =
    (p.x in 0 until numCols) && (p.y in 0 until numRows)

fun <T> Grid<T>.maybeGet(p: Point): T? =
    if (p in this) this[p]
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

inline fun <T> Grid<T>.forEachIndexed(action: (p: Point, T) -> Unit) {
    this.pointIndices.forEach { p ->
        action(p, this[p])
    }
}

fun <T> Grid<T>.count(predicate: (T) -> Boolean): Int =
    this.sumOf { row ->
        row.count(predicate)
    }

fun <T> Sequence<CharSequence>.parseGrid(transform: (Char) -> T): Grid<T> =
    this
        .mapTo(mutableListOf()) {
            it.mapTo(mutableListOf(), transform)
        }
        .let(::Grid)

fun <T> Iterable<CharSequence>.parseGrid(transform: (Char) -> T): Grid<T> =
    this
        .mapTo(mutableListOf()) {
            it.mapTo(mutableListOf(), transform)
        }
        .let(::Grid)

fun Iterable<CharSequence>.parseGrid(): Grid<Char> = this.parseGrid { it }

fun <T> Path.readGrid(transform: (Char) -> T): Grid<T> =
    this.useLines { lines ->
        lines.parseGrid(transform)
    }

fun Path.readGrid(): Grid<Char> = this.readGrid { it }

fun <T> Grid<T>.pointOfFirst(predicate: (T) -> Boolean): Point =
    pointIndices.first { p ->
        predicate(this[p])
    }

fun <T> Grid<T>.pointOfLast(predicate: (T) -> Boolean): Point =
    pointIndices.last { p ->
        predicate(this[p])
    }
