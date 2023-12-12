package com.groundsfam.advent.y2022.d17

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.points.Point
import com.groundsfam.advent.timed
import java.util.LinkedList
import kotlin.io.path.div
import kotlin.io.path.useLines


// interestingly, using only top 30 rows was not sufficient on my input
const val TOP = 100
// represents the state of the top `TOP` rows of the chamber, together with the next jet that will fire and
// the next shape that will fall
data class State(val jetIdx: Int, val shapeIdx: Int, val chamberTop: List<List<Boolean>>)

// shapes of falling rocks
val SHAPES = listOf(
    listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0)),               // minus shape
    listOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 2)),  // plus shape
    listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, 1), Point(2, 2)),  // backwards L shape
    listOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0 ,3)),               // vertical line shape
    listOf(Point(0, 0), Point(1, 0), Point(0, 1), Point(1, 1)),               // square shape
)


// Chamber is 7 units wide.
// Rocks appear with left edge 2 units away from left wall and bottom edge 3 units above highest rock in room.
fun simulateFallingRocks(directions: String, numRocks: Long): Long {
    var jet = 0
    var rockNum = 0

    // chamber[y][x] = whether a part of a rock occupies position (x, y) in the chamber.
    // we're using a linked list because we will continuously truncate the chamber to only the `TOP` rows.
    // adding new rows on the top and dropping truncated rows at the bottom are both O(1) operations for linked lists.
    // access is O(n), but since we're bounding n to be (just over) `TOP`, this is effectively constant too.
    val chamber = LinkedList<BooleanArray>()
    // we will only store the top few rows of the chamber, dropping the rest under the assumption
    // that they will not contribute to the tower height. this may not be valid in general, but it seems
    // to work in practice for sufficiently high values of `TOP`, usually around 100 or less
    var droppedRows = 0

    // check if the given shape can move such that its bottom left corner is now `newBl`
    fun canMove(newBl: Point, shape: List<Point>): Boolean =
        shape.map { it + newBl }.all {
            it.x in 0..6 &&
                it.y >= 0 &&
                (it.y >= chamber.size || !chamber[it.y][it.x])  // point is not occupied by fallen rock
        }

    // simulate dropping one new rock into the chamber
    fun dropRock() {
        // current position of bottom left hand corner of the current rock's rectangle
        var bl = Point(2, chamber.size + 3)
        val shape = SHAPES[rockNum % SHAPES.size]

        var falling = true
        while (falling) {
            val bl1 = bl +
                if (directions[jet] == '<') Point(-1, 0)
                else Point(1, 0)
            bl = if (canMove(bl1, shape)) bl1 else bl
            jet = (jet + 1) % directions.length

            val bl2 = bl + Point(0, -1)
            if (canMove(bl2, shape)) {
                bl = bl2
            } else {
                falling = false
            }
        }
        shape.map { it + bl }.forEach { (x, y) ->
            while (y >= chamber.size) {
                chamber.add(BooleanArray(7))
            }
            chamber[y][x] = true
        }
        // truncate the chamber, while tracking the number of rows we've truncated
        while (chamber.size > TOP) {
            chamber.removeFirst()
            droppedRows++
        }

        rockNum++
    }

    // this guarantees that all states considered have the same size (truncated) chamber
    while (chamber.size < TOP) {
        dropRock()
    }

    // we're searching for a repeated state in order to find the period of the pattern of falling rocks.
    // the keys of this map are states we've seen so far.
    // the values are the rockNum of the next rock that will fall.
    // once a state repeats, we subtract the current rockNum from the one stored in this map to find the period.
    val states = mutableMapOf<State, Int>()
    var nextState = State(jet, rockNum % SHAPES.size, chamber.takeLast(TOP).map { it.toList() })
    while (nextState !in states) {
        states[nextState] = rockNum
        dropRock()
        nextState = State(jet, rockNum % SHAPES.size, chamber.takeLast(TOP).map { it.toList() })
    }
    val period = rockNum - states[nextState]!!

    // compute how much taller the chamber gets in a single period
    val prevRows = droppedRows + chamber.size
    repeat(period) {
        dropRock()
    }
    val rowsPerPeriod = droppedRows + chamber.size - prevRows

    val periodRows = ((numRocks - rockNum) / period) * rowsPerPeriod
    // simulate the last few rocks to bring the total number of dropped rocks up to the desired number
    repeat(((numRocks - rockNum) % period).toInt()) {
        dropRock()
    }

    // droppedRows + chamber.size includes all the rocks dropped in the process of finding the repeating period
    // and the ones dropped at the very end.
    return periodRows + droppedRows + chamber.size
}

// for debug
fun drawChamber(chamber: List<BooleanArray>): String =
    chamber
        .reversed()
        .joinToString("\n") { row ->
            row.joinToString("") { if (it) "#" else "." }
        }



fun main() = timed {
    val directions = (DATAPATH / "2022/day17.txt").useLines { lines ->
        lines.first()
    }
    println("Part one: ${simulateFallingRocks(directions, 2022)}")
    println("Part two: ${simulateFallingRocks(directions, 1_000_000_000_000)}")
}
