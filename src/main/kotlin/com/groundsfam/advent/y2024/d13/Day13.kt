package com.groundsfam.advent.y2024.d13

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

// recreate minimal point class that supports longs
data class Point(val x: Long, val y: Long) {
    operator fun plus(that: Point) = Point(this.x + that.x, this.y + that.y)
}
data class Button(val dx: Long, val dy: Long)
data class ClawMachine(val a: Button, val b: Button, val prize: Point)

fun winPrize(machine: ClawMachine, partTwo: Boolean): Long? {
    val (a, b, _) = machine
    val prize =
        if (partTwo) machine.prize + Point(10_000_000_000_000, 10_000_000_000_000)
        else machine.prize

    // invert the matrix
    // ( a.dx b.dx )
    // ( a.dy b.dy )

    val det = (a.dx * b.dy - b.dx * a.dy)
    if (det == 0L) {
        throw RuntimeException("Need to handle degenerate case!")
    } else {
        val aPushes1 = b.dy * prize.x - b.dx * prize.y
        val bPushes1 = -a.dy * prize.x + a.dx * prize.y
        // check if integer number of pushes can reach the goal
        if (aPushes1 % det != 0L || bPushes1 % det != 0L) {
            return null
        }
        val aPushes = aPushes1 / det
        val bPushes = bPushes1 / det
        if (partTwo || (aPushes in 0..100 && bPushes in 0..100)) {
            return 3 * aPushes + bPushes
        }
        return null
    }
}

fun main() = timed {
    val machines = (DATAPATH / "2024/day13.txt").useLines { lines ->
        val ret = mutableListOf<ClawMachine>()
        val buttons = mutableListOf<Button>()
        lines.forEachIndexed { i, line ->
            val (x, y) =
                if (i % 4 == 3) {
                    listOf(0L, 0L)
                } else {
                    line.split(",? ".toRegex())
                        .takeLast(2)
                        .map { it.substring(2).toLong() }
                }
            when (i % 4) {
                0, 1 -> {
                    buttons.add(Button(x, y))
                }
                2 -> {
                    ret.add(ClawMachine(buttons[0], buttons[1], Point(x, y)))
                    buttons.clear()
                }
            }
        }
        ret
    }
    machines
        .sumOf { winPrize(it, false) ?: 0 }
        .also { println("Part one: $it") }
    machines
        .sumOf { winPrize(it, true) ?: 0 }
        .also { println("Part two: $it") }
}
