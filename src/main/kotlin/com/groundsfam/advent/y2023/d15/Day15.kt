package com.groundsfam.advent.y2023.d15

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.readLines


fun main() = timed {
    val steps = (DATAPATH / "2023/day15.txt").readLines().first()
        .split(",")
        .sumOf { step ->
            step.fold<Int>(0) { curr, c ->
                (curr + c.code) * 17 % 256
            }
        }
        .also { println("Part one: $it") }
}
