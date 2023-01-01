package advent.y2022.d17

import advent.DATAPATH
import advent.Point
import kotlin.io.path.div
import kotlin.io.path.useLines

// shapes of falling rocks
val SHAPES = listOf(
    listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(3, 0)),               // minus shape
    listOf(Point(1, 0), Point(0, 1), Point(1, 1), Point(2, 1), Point(1, 2)),  // plus shape
    listOf(Point(0, 0), Point(1, 0), Point(2, 0), Point(2, 1), Point(2, 2)),  // backwards L shape
    listOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(0 ,3)),               // vertical line shape
    listOf(Point(0, 0), Point(1, 0), Point(0, 1), Point(1, 1)),               // square shape
)

// 2022 rocks should be simulated
const val ROUNDS = 2022

// Chamber is 7 units wide.
// Rocks appear with left edge 2 units away from left wall and bottom edge 3 units above highest rock in room.

fun simulateFallingRocks(directions: String): Int {
    var jet = 0

    var shapeIdx = 0
    // chamber[y][x] = whether a part of a rock occupies position (x, y) in the chamber
    val chamber = mutableListOf<BooleanArray>()

    fun canMove(newBl: Point, shape: List<Point>): Boolean =
        shape.map { it + newBl }.all {
            it.x in 0..6 &&
                it.y >= 0 &&
                (it.y >= chamber.size || !chamber[it.y][it.x])  // point is not occupied by fallen rock
        }

    repeat(ROUNDS) {
        // current position of bottom left hand corner of the current rock's rectangle
        var bl = Point(2, chamber.size + 3)
        val shape = SHAPES[shapeIdx]

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

        shapeIdx = (shapeIdx + 1) % SHAPES.size
    }

    return chamber.size
}

fun drawChamber(chamber: List<BooleanArray>): String =
    chamber
        .reversed()
        .joinToString("\n") { row ->
            row.joinToString("") { if (it) "#" else "." }
        }



fun main() {
    val directions = (DATAPATH / "2022/day17.txt").useLines { lines ->
        lines.first()
    }
    println("Part one: ${simulateFallingRocks(directions)}")
}
