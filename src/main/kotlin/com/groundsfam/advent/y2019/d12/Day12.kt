package com.groundsfam.advent.y2019.d12

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.lcm
import com.groundsfam.advent.points.*
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines
import kotlin.math.abs

const val NUM_STEPS = 1000

data class State(val positions: List<Int>, val velocities: List<Int>)

fun step(state: State): State {
    val positions = state.positions
    val velocities = state.velocities.toMutableList()

    positions.forEachIndexed { i, p ->
        (i + 1 until positions.size).forEach { j ->
            val q = positions[j]
            val dv = when {
                p < q -> 1
                p > q -> -1
                else -> 0
            }
            velocities[i] += dv
            velocities[j] -= dv
        }
    }

    return State(
        positions.mapIndexed { i, p -> p + velocities[i] },
        velocities
    )
}

fun simulateMoons(initPositions: List<Point3>): Int {
    val positions = initPositions.toTypedArray()
    val velocities = Array(initPositions.size) { Point3(0, 0, 0) }

    repeat(NUM_STEPS) {
        val newStates = (0..2).map { i ->
            State(positions.map { it[i] }, velocities.map { it[i] })
                .let(::step)
        }
        positions.indices.forEach { i ->
            positions[i] = Point3(
                newStates[0].positions[i],
                newStates[1].positions[i],
                newStates[2].positions[i],
            )
            velocities[i] = Point3(
                newStates[0].velocities[i],
                newStates[1].velocities[i],
                newStates[2].velocities[i],
            )
        }
    }

    return positions.indices.sumOf { i ->
        val (x, y, z) = positions[i]
        val (vx, vy, vz) = velocities[i]
        (abs(x) + abs(y) + abs(z)) * (abs(vx) + abs(vy) + abs(vz))
    }
}

// find state loop in one coordinate
// observation from input: the state always
// loops back to the initial state, hence
// no need to detect a loop in general
fun findLoop1(initPositions: List<Int>): Int {
    val initState = State(initPositions, initPositions.map { 0 })
    var state = step(initState)
    var steps = 1

    while (state != initState) {
        state = step(state)
        steps++
    }
    return steps
}

fun findLoop(initPositions: List<Point3>): Long =
    (0..2)
        .map { i ->
            findLoop1(initPositions.map { it[i] }).toLong()
        }
        .reduce(::lcm)



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
    println("Part two: ${findLoop(positions)}")
}
