package com.groundsfam.advent.y2019.d12

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.points.*
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs

const val NUM_STEPS = 1000

// change in velocity of some coordinate
// based on positions of those moons
fun dv(a: Int, b: Int): Int = when {
    a < b -> 1
    a > b -> -1
    else -> 0
}

fun simulateMoons(initPositions: List<Point3>): Int {
    val positions = initPositions.toTypedArray()
    val velocities = Array(initPositions.size) { Point3(0, 0, 0) }

    repeat(NUM_STEPS) {
        positions.forEachIndexed { i, p ->
            (i + 1 until positions.size).forEach { j ->
                val q = positions[j]
                val dv = Point3(
                    dv(p.x, q.x),
                    dv(p.y, q.y),
                    dv(p.z, q.z),
                )
                velocities[i] += dv
                velocities[j] -= dv
            }
        }
        velocities.forEachIndexed { i, v ->
            positions[i] += v
        }
    }

    return positions.indices.sumOf { i ->
        val (x, y, z) = positions[i]
        val (vx, vy, vz) = velocities[i]
        (abs(x) + abs(y) + abs(z)) * (abs(vx) + abs(vy) + abs(vz))
    }
}

fun main() = timed {
    val positions = (DATAPATH / "2019/day12.txt").useLines { lines ->
        lines.mapTo(mutableListOf()) { line ->
            line.substring(1 until line.length - 1)
                .split(", ")
                .let { (x, y, z) ->
                    Point3(
                        x.substring(2).toInt(),
                        y.substring(2).toInt(),
                        z.substring(2).toInt()
                    )
                }
        }
    }
    println("Part one: ${simulateMoons(positions)}")
}
