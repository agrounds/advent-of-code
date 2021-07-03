package advent.y2020

import java.lang.RuntimeException
import kotlin.io.path.div
import kotlin.io.path.useLines

private enum class Space {
    FLOOR, UNOCCUPIED, OCCUPIED;

    companion object {
        fun fromChar(c: Char): Space =
            when (c) {
                'L' -> UNOCCUPIED
                '.' -> FLOOR
                '#' -> OCCUPIED
                else -> throw RuntimeException("Illegal char: $c")
            }
    }
}

private typealias Grid = List<List<Space>>

private fun Grid.toMutable(): MutableList<MutableList<Space>> {
    val self = this
    return mutableListOf<MutableList<Space>>().apply {
        self.forEach { row ->
            add(mutableListOf<Space>().apply {
                addAll(row)
            })
        }
    }
}

private fun Grid.countAdjacentOccupants(x: Int, y: Int): Int {
    var count = 0
    for (i in (x - 1)..(x + 1)) {
        for (j in (y - 1)..(y + 1)) {
            if (
                i in 0 until size &&
                j in 0 until this[i].size &&
                (i != x || j != y) &&
                this[i][j] == Space.OCCUPIED
            ) {
                count++
            }
        }
    }
    return count
}

private fun Grid.countVisibleOccupants(x: Int, y: Int): Int {
    fun visibleOccupant(dx: Int, dy: Int): Boolean {
        if (dx == 0 && dy == 0) return false
        var i = x + dx
        var j = y + dy
        while (i in 0 until size && j in 0 until this[i].size) {
            when (this[i][j]) {
                Space.OCCUPIED -> return true
                Space.UNOCCUPIED -> return false
                Space.FLOOR -> {} // do nothing
            }
            i += dx
            j += dy
        }
        return false
    }

    var count = 0
    for (dx in -1..1) {
        for (dy in -1..1) {
            if (visibleOccupant(dx, dy)) count++
        }
    }
    return count
}

private fun processGrid(lines: Sequence<String>): Grid =
    lines.mapTo(mutableListOf()) { line ->
        line.map { Space.fromChar(it) }
    }

private fun simulateOccupants1(grid: Grid): Grid {
    val mutableGrid = grid.toMutable()

    var changes = mutableListOf((0 to 0) to Space.FLOOR)  // dummy value to get the loop started
    while (changes.isNotEmpty()) {
        changes = mutableListOf()
        mutableGrid.forEachIndexed { i, row ->
            row.forEachIndexed { j, s ->
                when (s) {
                    Space.OCCUPIED ->
                        if (mutableGrid.countAdjacentOccupants(i, j) >= 4) {
                            changes.add((i to j) to Space.UNOCCUPIED)
                        }
                    Space.UNOCCUPIED ->
                        if (mutableGrid.countAdjacentOccupants(i, j) == 0) {
                            changes.add((i to j) to Space.OCCUPIED)
                        }
                    Space.FLOOR -> {} // do nothing
                }
            }
        }
        changes.forEach { (coord, newSpace) ->
            val (x, y) = coord
            mutableGrid[x][y] = newSpace
        }
    }

    return mutableGrid
}

private fun simulateOccupants2(grid: Grid): Grid {
    val mutableGrid = grid.toMutable()

    var changes = mutableListOf((0 to 0) to Space.FLOOR)  // dummy value to get the loop started
    while (changes.isNotEmpty()) {
        changes = mutableListOf()
        mutableGrid.forEachIndexed { i, row ->
            row.forEachIndexed { j, s ->
                when (s) {
                    Space.OCCUPIED ->
                        if (mutableGrid.countVisibleOccupants(i, j) >= 5) {
                            changes.add((i to j) to Space.UNOCCUPIED)
                        }
                    Space.UNOCCUPIED ->
                        if (mutableGrid.countVisibleOccupants(i, j) == 0) {
                            changes.add((i to j) to Space.OCCUPIED)
                        }
                    Space.FLOOR -> {} // do nothing
                }
            }
        }
        changes.forEach { (coord, newSpace) ->
            val (x, y) = coord
            mutableGrid[x][y] = newSpace
        }
    }

    return mutableGrid
}

fun main() {
    val grid = (DATAPATH / "day11.txt").useLines { processGrid(it) }

    simulateOccupants1(grid).let { it.flatten().count { s -> s == Space.OCCUPIED } }
        .also { println("Part one: $it") }

    simulateOccupants2(grid).let { it.flatten().count { s -> s == Space.OCCUPIED } }
        .also { println("Part two: $it") }
}