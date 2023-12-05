package com.groundsfam.advent.y2022.d01

import com.groundsfam.advent.DATAPATH
import com.groundsfam.advent.timed
import kotlin.io.path.div
import kotlin.io.path.useLines


fun main() = timed {
    val sums = (DATAPATH / "2022/day01.txt").useLines { lines ->
        lines.toList().fold(mutableListOf(0)) { sums, line ->
            if (line.isEmpty()) {
                sums.add(0)
            } else {
                sums[sums.size - 1] = sums.last() + line.toInt()
            }
            sums
        }
    }
    println(sums.sorted().takeLast(3).sum())
}
