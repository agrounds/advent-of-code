package com.groundsfam.advent.y2023.d24

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines

const val LO = 200_000_000_000_000.0
const val HI = 400_000_000_000_000.0

data class Point(val x: Long, val y: Long, val z: Long)

fun pathsCross(a: Pair<Point, Point>, b: Pair<Point, Point>): Boolean {
    val (ax, ay, adx, ady) = a.let { (p, v) -> listOf(p.x, p.y, v.x, v.y) }
    val (bx, by, bdx, bdy) = b.let { (p, v) -> listOf(p.x, p.y, v.x, v.y) }

    // solving the linear system in variables (t1, t2)
    // ax + t1 * adx = bx + t2 * bdx
    // ay + t1 * ady = by + t2 * bdy
    val det = ady * bdx - adx * bdy
    if (det == 0L) {
        // degenerate case: either one or both hailstones are not moving, or the two lines
        // are parallel or coincide
        if (adx == 0L && ady == 0L) {
            // a is not moving - must be in bounds
            if (ax.toDouble() !in LO..HI || ay.toDouble() !in LO..HI) {
                return false
            }
            if (bdx == 0L && bdy == 0L) {
                // b is also not moving
                return ax == bx && ay == by
            }
            // check if b's line goes through a's position
            return bdx * (by - ay) == bdy * (bx - ax)
        }
        if (bdx == 0L && bdy == 0L) {
            // b is not moving - must be in bounds
            if (bx.toDouble() !in LO..HI || by.toDouble() !in LO..HI) {
                return false
            }
        }
        // check if a's line goes through b's (initial) position
        return adx * (by - ay) == ady * (bx - ax)
    }
    // nondegenerate case: find intersection point
    val t1 = (bdx * (by - ay) - bdy * (bx - ax)).toDouble() / det
    val t2 = (adx * (by - ay) - ady * (bx - ax)).toDouble() / det
    if (t1 < 0 || t2 < 0) return false
    // intersection point
    val ix = ax + t1 * adx
    val iy = ay + t1 * ady
    return ix in LO..HI && iy in LO..HI
}

fun main() = timed {
    val hailstones = (DATAPATH / "2023/day24.txt").useLines { lines ->
        // list of pairs of points, pairs are (position, velocity)
        lines.mapTo(mutableListOf()) { line ->
            val parts = line.split(""",?(\s+@)?\s+""".toRegex()).map(String::toLong)
            Point(parts[0], parts[1], parts[2]) to Point(parts[3], parts[4], parts[5])
        }
    }
    hailstones
        .foldIndexed(0) { i, count, a ->
            count + (i + 1 until hailstones.size).count { j ->
                pathsCross(a, hailstones[j])
            }
        }
        .also { println("Part one: $it") }
}
